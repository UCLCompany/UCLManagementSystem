package ucl.group.talentManageSystem.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@ServletComponentScan
@ComponentScan(basePackages = {"ucl.group.talentManageSystem.api", "ucl.group.excelSystem.api"})
@MapperScan(basePackages = {"ucl.group.talentManageSystem.api.db.dao","ucl.group.excelSystem.api.db.dao"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}