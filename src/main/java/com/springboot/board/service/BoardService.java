package com.springboot.board.service;

import com.springboot.board.entity.Board;
import com.springboot.board.entity.Like;
import com.springboot.board.repository.BoardRepository;
import com.springboot.board.repository.LikeRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.reply.service.ReplyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final ReplyService replyService;
    private final LikeRepository likeRepository;
    public BoardService(BoardRepository boardRepository, ReplyService replyService, LikeRepository likeRepository) {
        this.boardRepository = boardRepository;
        this.replyService = replyService;
        this.likeRepository = likeRepository;
    }

    public Board createBoard(Board board){
        return boardRepository.save(board);
    }
    public Board createLike(Board board){

        if(board.getLike() == null){
            Like like = new Like();
            like.setMember(board.getMember());
            board.setLike(like);
        }else {
            likeRepository.delete(board.getLike());
            board.clearLike();
        }
        return boardRepository.save(board);
    }
    public void deleteLike(Board board){
        likeRepository.delete(board.getLike());
    }
    public Board updateBoard(Board board){
        Board findBoard = findVerifiedBoard(board.getBoardId());

        Optional.ofNullable(board.getTitle())
                .ifPresent(title -> findBoard.setTitle(title));
        Optional.ofNullable(board.getContent())
                .ifPresent(content -> findBoard.setContent(content));
        Optional.ofNullable(board.getPrivacyStatus())
                .ifPresent(privacyStatus -> findBoard.setPrivacyStatus(privacyStatus));

        board.setModifiedAt(LocalDateTime.now());

        return boardRepository.save(findBoard);
    }
    public Board findBoard(long boardId){
        Board board = findVerifiedBoard(boardId);
        if(board.getQuestionStatus() == Board.QuestionStatus.QUESTION_DELETED){
            throw new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND);
        }
        return findVerifiedBoard(boardId);
    }
    public Page<Board> findBoards(int page, int size){
        return boardRepository.findAll(PageRequest.of(page,size, Sort.Direction.DESC,"board-id"));
    }

    public Board findVerifiedBoard(long boardId){
        Optional<Board> Board = boardRepository.findById(boardId);
        return Board.orElseThrow(()-> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
    }

    public void deleteBoard(long boardId){
        Board findBoard = findVerifiedBoard(boardId);
        findBoard.setQuestionStatus(Board.QuestionStatus.QUESTION_DELETED);

        if(findBoard.getReply() != null) {
            replyService.deleteReply(findBoard.getReply().getReplyId());
        }
        boardRepository.save(findBoard);
    }

}
