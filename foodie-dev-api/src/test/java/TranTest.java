import com.hins.Application;
import com.hins.service.StuService;
import com.hins.service.TestTransService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-09-17
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
public class TranTest {

    @Autowired
    private StuService stuService;

    @Autowired
    private TestTransService testTransService;

//    @Test
    public void myTest() {
//        stuService.testPropagationTrans();
        testTransService.testPropagationTrans();
    }




}
