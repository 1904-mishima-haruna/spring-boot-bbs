package com.example.forum.service;
import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Comment;
import com.example.forum.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ReportRepository reportRepository;


    /*
     * レコード全件取得処理
     */
    public List<CommentForm> findAllComment() {
        List<Comment> results = commentRepository.findAllByOrderByIdDesc();
        List<CommentForm> comments = setCommentForm(results);
        return comments;
    }
    /*
     * DBから取得したデータをFormに設定
     */
    private List<CommentForm> setCommentForm(List<Comment> results) {
        List<CommentForm> comments = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            CommentForm comment = new CommentForm();
            Comment result = results.get(i);
            comment.setId(result.getId());
            comment.setReContent(result.getReContent());
            comment.setContentId(result.getContentId());
            comment.setUpdatedDate(result.getUpdatedDate());
            comments.add(comment);
        }
        return comments;
    }

    /*
     * レコード追加
     */
    public void saveComment(CommentForm reqComment, ReportForm reqReport) throws ParseException {
        Comment saveComment = setCommentEntity(reqComment);
        commentRepository.save(saveComment);
        //投稿の更新日時のみを更新させたい
        //commentFormのcontentIdをreportFormのidにセット。viewでやらなくてもここでやればいい
        if(reqReport.getContent() != null) {
            reqReport.setId(reqComment.getContentId());
            Report saveReport = new ReportService().setReportEntity(reqReport);
            reportRepository.save(saveReport);
        }
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Comment setCommentEntity(CommentForm reqComment) throws ParseException {
        Comment comment = new Comment();
        comment.setId(reqComment.getId());
        comment.setReContent(reqComment.getReContent());
        comment.setContentId(reqComment.getContentId());
        //UpdatedDateには現在時刻をセット
        Date date = new Date();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = sdFormat.format(date);
        Date currentDate = sdFormat.parse(current);
        comment.setUpdatedDate(currentDate);
        return comment;
    }

    /*
     * レコード1件取得
     */
    public CommentForm editComment(Integer id) {
        List<Comment> results = new ArrayList<>();
        results.add((Comment) commentRepository.findById(id).orElse(null));
        List<CommentForm> comments = setCommentForm(results);
        return comments.get(0);
    }

    /*
     * 削除機能
     */
    public void deleteComment(Integer id) {
        commentRepository.deleteById(id);
    }
}
