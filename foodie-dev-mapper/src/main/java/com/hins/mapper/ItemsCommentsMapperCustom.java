package com.hins.mapper;


import com.hins.pojo.vo.ItemCommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsCommentsMapperCustom {

    List<ItemCommentVO> selectItemComments(@Param("paramMap") Map<String, Object> paramMap);
}