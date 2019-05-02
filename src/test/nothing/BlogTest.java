import com.yuan.blog.BlogApplication;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BlogApplication.class)
public class BlogTest {
    @Autowired
    StringEncryptor encryptor;

    @Test
    public void getPass() {
        System.out.println("=================");
        System.out.println(this.encryptor.encrypt("2.56465466b10tUaoC"));
        System.out.println(this.encryptor.encrypt("jdbc:mysql://**.197:3306/blog?serverTimezone=GMT%2B8&useSSL=false&characterEncoding=UTF-8"));
        System.out.println("=================");
    }
}
