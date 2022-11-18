/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan;

import com.ethan.entity.CommentDO;
import com.ethan.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/15/2022 4:37 PM
 */
@SpringBootTest(classes = JPAApplication.class)
public class PessimisticAndOptimisticLockTest {
    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Test
    void testLock() throws InterruptedException {
        CommentDO commentDO = new CommentDO();
        commentDO.setCommentParentId(0L);
        commentDO.setBloggerId(1L);
        commentDO.setBlogId(1L);
        commentDO.setCommentContent("testing");
        commentDO.setCommentTime(LocalDateTime.now());
        commentDO.setCommentLikes(0L);
        commentService.postComment(commentDO);

        Long commentId = commentDO.getCommentId();
        System.out.println("start commentId:" + commentId);
        // ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            taskExecutor.execute(() -> {
                commentService.likeComment(commentId);
                // commentService.likeCommentPessimistic(commentId);
            });
        }
        TimeUnit.MINUTES.sleep(5);
        // executorService.shutdown();
    }
}


