import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ComponentScan(basePackages = "com.dalhousie.Neighbourly")
public class NeighbourlyApplicationTestRunner {

}