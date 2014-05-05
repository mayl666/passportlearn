package com.sogou.upd.passport.common.base;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Ignore;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * 动态编译java源文件测试
 * User: shipengzhi
 * Date: 13-6-20
 * Time: 下午12:09
 */
@Ignore
public class JavaCompilerTest {

    public static void main(String[] args) throws Exception {
        Date date = DateUtils.addDays(new Date(), 7);
        System.out.println(date);
        System.out.println(date.toString());
//        String source = "public class Main { public static void main(String[] args) {System.out.println(\"Hello World!\");} }";
//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
//        StringSourceJavaObject sourceObject = new JavaCompilerTest.StringSourceJavaObject("Main", source);
//        Iterable<? extends JavaFileObject> fileObjects = Arrays.asList(sourceObject);
//        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, fileObjects);
//        boolean result = task.call();
//        if (result) {
//            System.out.println("编译成功。");
//        }
    }

    static class StringSourceJavaObject extends SimpleJavaFileObject {

        private String content = null;

        public StringSourceJavaObject(String name, String content) throws URISyntaxException

        {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.content = content;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return content;
        }
    }
}
