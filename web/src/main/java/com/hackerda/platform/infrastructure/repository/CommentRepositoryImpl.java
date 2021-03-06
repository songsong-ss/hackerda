package com.hackerda.platform.infrastructure.repository;

import com.google.common.collect.Lists;
import com.hackerda.platform.domain.community.*;
import com.hackerda.platform.infrastructure.database.mapper.CommentMapper;
import com.hackerda.platform.infrastructure.database.model.Comment;
import com.hackerda.platform.infrastructure.database.model.CommentExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private PosterRepository posterRepository;


    @Override
    public void save(CommentBO commentBO) {
        Comment comment = getCommentDO(commentBO);

        commentMapper.insertSelective(comment);

        commentBO.setId(comment.getId());
    }

    @Override
    public List<CommentDetailBO> findDetailByPostId(long postId) {

        CommentExample example = new CommentExample();
        example.createCriteria().andPostIdEqualTo(postId);

        List<Comment> commentList = commentMapper.selectByExample(example);

        return toDetailBO(commentList);

    }

    @Override
    public List<CommentBO> findByIdList(List<Long> idList) {
        if(idList.isEmpty()) {
            return Collections.emptyList();
        }

        CommentExample example = new CommentExample();
        example.createCriteria().andIdIn(idList);

        return commentMapper.selectByExample(example).stream().map(x-> {

            IdentityCategory identity = IdentityCategory.getByCode(x.getIdentityCode());
            RecordStatus recordStatus = RecordStatus.getByCode(x.getRecordStatus());
            CommentBO commentBO = new CommentBO(x.getPostId(), x.getPostUserName(), x.getUserName(), x.getContent(),
                    x.getReplyCommentId(), x.getRootCommentId(), x.getPostTime(), recordStatus, identity, x.getReplyUserName());

            commentBO.setId(x.getId());
            return commentBO;
        }).collect(Collectors.toList());

    }

    @Override
    public void update(CommentBO commentBO) {
        Comment comment = getCommentDO(commentBO);
        comment.setId(commentBO.getId());

        commentMapper.updateByPrimaryKeySelective(comment);
    }

    private Comment getCommentDO(CommentBO commentBO) {
        Comment comment = new Comment();
        comment.setContent(commentBO.getContent());
        comment.setUserName(commentBO.getUserName());
        comment.setIdentityCode(commentBO.getIdentityCategory().getCode());
        comment.setPostTime(commentBO.getPostTime());
        comment.setPostUserName(commentBO.getPostUserName());
        comment.setPostId(commentBO.getPostId());
        comment.setRecordStatus(commentBO.getStatus().getCode());
        comment.setRootCommentId(commentBO.getRootCommentId());
        comment.setReplyCommentId(commentBO.getReplyCommentId());
        comment.setReplyUserName(commentBO.getReplyUserName());
        return comment;
    }

    @Override
    public List<CommentDetailBO> findByPost(RecordStatus status, long postId) {
        CommentExample example = new CommentExample();
        example.createCriteria()
                .andPostIdEqualTo(postId)
                .andRecordStatusEqualTo(status.getCode());

        List<Comment> commentList = commentMapper.selectByExample(example);
        return toDetailBO(commentList);

    }

    @Override
    public List<CommentDetailBO> findByPostUserName(RecordStatus status, String userName) {
        CommentExample example = new CommentExample();
        example.createCriteria()
                .andUserNameEqualTo(userName)
                .andRecordStatusEqualTo(status.getCode());

        List<Comment> commentList = commentMapper.selectByExample(example);
        return toDetailBO(commentList);

    }

    @Override
    public List<CommentDetailBO> findByReplyUserName(RecordStatus status, String userName) {
        CommentExample example = new CommentExample();
        example.createCriteria()
                .andReplyUserNameEqualTo(userName)
                .andRecordStatusEqualTo(status.getCode());

        List<Comment> commentList = commentMapper.selectByExample(example);
        return toDetailBO(commentList);
    }

    @Override
    public long count(RecordStatus recordStatus) {
        CommentExample example = new CommentExample();
        example.createCriteria().andRecordStatusEqualTo(recordStatus.getCode());

        return commentMapper.countByExample(example);
    }


    private List<CommentDetailBO> toDetailBO(List<Comment> commentList ) {

        List<String> collect = commentList.stream().map(Comment::getUserName).collect(Collectors.toList());
        Map<String, StudentPoster> studentPosterMap = posterRepository.findStudentPosterByUserName(collect).stream().collect(Collectors.toMap(Poster::getUserName,
                x -> x));

        List<CommentDetailBO> detailBOList = commentList.stream().map(x -> {
            StudentPoster poster = studentPosterMap.get(x.getUserName());
            IdentityCategory identity = IdentityCategory.getByCode(x.getIdentityCode());
            RecordStatus recordStatus = RecordStatus.getByCode(x.getRecordStatus());

            return new CommentDetailBO(x.getId(), x.getPostId(), x.getPostUserName(), poster, x.getContent(),
                    x.getReplyCommentId(),
                    x.getRootCommentId(), x.getPostTime(), recordStatus, identity, x.getLikeCount(), x.getReplyUserName());


        }).collect(Collectors.toList());

        // 根据展示的名称分组
        Map<String, List<CommentDetailBO>> showNameMap = detailBOList.stream().collect(Collectors.groupingBy(CommentDetailBO::getShowUserName,
                Collectors.mapping(x -> x, Collectors.toList())));

        for (List<CommentDetailBO> value : showNameMap.values()) {
            // 在展示的名称中根据userName分组
            Map<String, List<CommentDetailBO>> userNameMap =
                    value.stream().collect(Collectors.groupingBy(CommentDetailBO::getUserName,
                            Collectors.mapping(x -> x, Collectors.toList())));

            value.sort(Comparator.comparing(CommentDetailBO :: getPostTime).reversed());

            List<Map.Entry<String, List<CommentDetailBO>>> entries = Lists.newArrayList(userNameMap.entrySet());

            entries.sort(Comparator.comparing(o -> o.getValue().get(0).getPostTime()));

            for(int i=0; i< entries.size(); i++) {
                int finalI = entries.size() == 1 ? 0 : i+1;
                entries.get(i).getValue().forEach(post -> post.setUserShowNameOrder(finalI));
            }
        }

        return detailBOList;
    }
}
