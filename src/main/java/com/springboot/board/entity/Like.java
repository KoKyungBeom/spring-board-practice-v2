package com.springboot.board.entity;

import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long likeId;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToOne
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    public void setMember(Member member){
        if(!member.getLikes().contains(this)){
            member.getLikes().add(this);
        }
        this.member = member;
    }
    public void setBoard(Board board){
        this.board = board;
        if(board.getLike() != this){
            board.setLike(this);
        }
    }
}
