package com.sogou.upd.passport;

import junit.framework.TestCase;
import org.junit.Ignore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-19 Time: 上午1:02 To change this template
 * use File | Settings | File Templates.
 */
@Ignore
public class RoseScannerTest extends TestCase {

  private String path = "file:/D:/workspace/upd/sogou-passport/sogou-passport/passport-main-web/target/classes/**/*DAO.class";

  public void testGetResources(){
    ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    try {
      Resource[] metaInfResources = resourcePatternResolver
          .getResources("classpath*:**/*DAO.class");
      for(Resource r : metaInfResources){
        System.out.println("URL:" + r.getURL());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
