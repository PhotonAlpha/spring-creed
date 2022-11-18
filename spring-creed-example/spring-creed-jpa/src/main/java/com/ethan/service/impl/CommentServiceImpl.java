/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.service.impl;

import com.ethan.dao.CommentDao;
import com.ethan.entity.CommentDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/15/2022 4:29 PM
 */
@Service
public class CommentServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);
    @Resource
    private CommentDao commentDao;

    public void postComment(CommentDO comment) {
        commentDao.save(comment);
    }

    @Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void likeComment(Long commentId) {
        log.info("thread:{} start like comment:{}", Thread.currentThread().getName(), commentId);
        try {
            Optional<CommentDO> commentOptional = commentDao.findById(commentId);
            if (commentOptional.isPresent()) {
                CommentDO commentDO = commentOptional.get();
                commentDO.setCommentLikes(commentDO.getCommentLikes() + 1);
                commentDao.save(commentDO);
            }
        } catch (Exception e) {
            // log.error(Thread.currentThread().getName()+" update error", e);
            throw e;
        }
    }


    @Recover
    public void likeCommentFallback(Exception e, Long commentId) {
        log.warn("likeCommentFallback with id:{} and error msg:{}", commentId, e.getMessage());
    }


    @Transactional(rollbackFor = Exception.class)
    public void likeCommentPessimistic(Long commentId) {
        log.info("thread:{} start pessimistic like comment:{}", Thread.currentThread().getName(), commentId);
        try {
            Optional<CommentDO> commentOptional = commentDao.findByBlogIdWithPessimisticLock(commentId);
            if (commentOptional.isPresent()) {
                CommentDO commentDO = commentOptional.get();
                commentDO.setCommentLikes(commentDO.getCommentLikes() + 1);
                TimeUnit.SECONDS.sleep(1);
                commentDao.save(commentDO);
            }
        } catch (InterruptedException ex) {
            log.error(Thread.currentThread().getName()+" InterruptedException error", ex.getMessage());
        } catch (Exception e) {
            log.error(Thread.currentThread().getName()+" update error", e.getMessage());
            throw e;
        }
    }

}
