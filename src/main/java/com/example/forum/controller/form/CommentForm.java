package com.example.forum.controller.form;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class CommentForm implements Serializable {
    private int id;

    /*@NotEmpty(message = "コメントを入力してください")*/
    private String reContent;

    private int contentId;

    private Date createdDate;

    private Date updatedDate;
}
