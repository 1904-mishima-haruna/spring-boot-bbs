package com.example.forum.controller;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.service.CommentService;
import com.example.forum.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;
    @Autowired
    CommentService commentService;
    @Autowired
    HttpSession session;

    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top(@RequestParam(name = "start", required = false) String start
            , @RequestParam(name = "end", required = false) String end) throws ParseException {
        ModelAndView mav = new ModelAndView();
        // 投稿とコメントを全件取得
        List<ReportForm> contentData = reportService.findAllReport(start, end);
        List<CommentForm> commentData = commentService.findAllComment();
        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを保管
        mav.addObject("contents", contentData);
        mav.addObject("comments", commentData);
        mav.addObject("start", start);
        mav.addObject("end", end);

        //バリデーション処理。セッションの値を取得しaddObject
        List<Object> errorList = new ArrayList<>();
        errorList.add(session.getAttribute("validationError"));
        mav.addObject("validationError", errorList);
        //表示指定
        Object reportID = session.getAttribute("reportID");
        mav.addObject("reportID", reportID);

        session.invalidate();

        return mav;
    }

    /*
     * 新規投稿画面表示
     */
    @GetMapping("/new")
    public ModelAndView newContent() {
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        ReportForm reportForm = new ReportForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", reportForm);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@ModelAttribute("formModel") ReportForm reportForm) {
        // 投稿をテーブルに格納
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 編集画面表示処理
     */
    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        // 編集する投稿を取得
        ReportForm report = reportService.editReport(id);
        // 編集する投稿をセット
        mav.addObject("formModel", report);
        // 画面遷移先を指定
        mav.setViewName("/edit");
        return mav;
    }

    /*
     * 編集処理
     */
    @PutMapping("/update/{id}")
    public ModelAndView updateContent(@PathVariable Integer id,
                                      @ModelAttribute("formModel") ReportForm report) {
        // UrlParameterのidを更新するentityにセット
        report.setId(id);
        // 編集した投稿を更新
        reportService.saveReport(report);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント処理
     */
    @PostMapping("/comment")
    public ModelAndView addComment(@Validated @ModelAttribute("formModel") CommentForm commentForm, BindingResult result, ReportForm reportForm) throws ParseException {
        //バリデーション処理
        if (result.hasErrors()) {
            List<String> errorList = new ArrayList<String>();
            for (ObjectError error : result.getAllErrors()) {
                errorList.add(error.getDefaultMessage());
            }
            session.setAttribute("validationError", errorList);
            //表示指定
            session.setAttribute("reportID", commentForm.getContentId());
            return new ModelAndView("redirect:/");
        }

        // 投稿をテーブルに格納
        commentService.saveComment(commentForm, reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント編集画面表示
     */
    @GetMapping("/commentEdit/{id}")
    public ModelAndView CommentEditContent(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        // 編集する投稿を取得。戻り値としてreportオブジェクトが返ってくる
        CommentForm comment = commentService.editComment(id);
        // 編集する投稿をセット
        mav.addObject("formModel", comment);
        // 画面遷移先を指定
        mav.setViewName("/commentEdit");

        //バリデーション処理。セッションの値を取得しaddObject
        List<Object> errorList = new ArrayList<>();
        errorList.add(session.getAttribute("validationError"));
        mav.addObject("validationError", errorList);
        session.invalidate();

        return mav;
    }

    /*
     * コメント編集処理
     */
    @PostMapping("/commentEdit/{id}")
    public ModelAndView editReContent(@PathVariable Integer id, @Validated @ModelAttribute("formModel") CommentForm commentForm, BindingResult result, ReportForm reportForm) throws ParseException {
        //バリデーション処理
        if (result.hasErrors()) {
            List<String> errorList = new ArrayList<String>();
            for (ObjectError error : result.getAllErrors()) {
                errorList.add(error.getDefaultMessage());
            }
            session.setAttribute("validationError", errorList);
            return new ModelAndView("redirect:/commentEdit/{id}");
        }

        // 投稿をテーブルに格納
        commentService.saveComment(commentForm, reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント削除処理
     */
    @PostMapping("/commentDelete/{id}")
    public ModelAndView deleteComment(@PathVariable("id") Integer id) {
        // 投稿をテーブルに格納
        commentService.deleteComment(id);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }


}
