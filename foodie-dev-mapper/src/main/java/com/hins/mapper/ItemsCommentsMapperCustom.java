package com.hins.mapper;


import com.hins.pojo.vo.ItemCommentVO;
import com.hins.pojo.vo.MyCommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsCommentsMapperCustom {

    List<ItemCommentVO> selectItemComments(@Param("paramMap") Map<String, Object> paramMap);

    void saveCommets(Map<String, Object> map);

    List<MyCommentVO> selectMyCommentVOs(String userId);
}