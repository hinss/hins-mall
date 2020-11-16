package com.hins;

import com.hins.pojo.Stu;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: hins
 * @created: 2020-11-16 13:41
 * @desc:
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class ESTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 不建议使用 ElasticsearchTemplate 对索引进行管理（创建索引，更新映射，删除索引）
     * 索引就像是数据库或者数据库中的表，我们平时是不会是通过java代码频繁的去创建修改删除数据库或者表的
     * 我们只会针对数据做CRUD的操作
     * 在es中也是同理，我们尽量使用 ElasticsearchTemplate 对文档数据做CRUD的操作
     * 1. 属性（FieldType）类型不灵活
     * 2. 主分片与副本分片数无法设置
     */

    @Test
    public void createIndexStu(){

        Stu stu = new Stu();
        stu.setStuId(1003L);
        stu.setName("raymond");
        stu.setAge(25);
        stu.setMoney(88888888f);
        stu.setSign("I save people");
        stu.setDescription("I save people");

        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        elasticsearchTemplate.index(indexQuery);
    }

    @Test
    public void deleteIndexStu() {
        elasticsearchTemplate.deleteIndex(Stu.class);
    }

    @Test
    public void updateStuDoc() {

        Map<String, Object> sourceMap = new HashMap<>();
//        sourceMap.put("sign", "I am not super man");
        sourceMap.put("money", 99.8f);
//        sourceMap.put("age", 33);

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(sourceMap);

        UpdateQuery updateQuery = new UpdateQueryBuilder()
                .withClass(Stu.class)
                .withId("1004")
                .withIndexRequest(indexRequest)
                .build();

//        update stu set sign='abc',age=33,money=88.6 where docId='1002'

        elasticsearchTemplate.update(updateQuery);
    }

    @Test
    public void searchStuDoc() {

        Pageable pageable = PageRequest.of(0, 5);

        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description", "army man"))
                .withPageable(pageable)
                .build();
        AggregatedPage<Stu> pagedStu = elasticsearchTemplate.queryForPage(query, Stu.class);
        System.out.println("检索后的总分页数目为：" + pagedStu.getTotalPages());
        List<Stu> stuList = pagedStu.getContent();
        for (Stu s : stuList) {
            System.out.println(s);
        }

    }








}
