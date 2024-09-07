package com.fuzzy;

import junit.framework.TestCase;

import java.io.File;
import java.nio.file.Path;

public class FilesTest extends TestCase {


   public void test(){
       File file = new File("start.bat");
       String absolutePath = file.getAbsolutePath();
       System.out.println(absolutePath);
       Path relativize = file.toPath().resolveSibling(Path.of("C:\\Users\\FuzzY\\IdeaProjects\\"));
       System.out.println(relativize.toString());
   }


}
