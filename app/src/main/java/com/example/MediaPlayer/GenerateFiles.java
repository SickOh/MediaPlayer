package com.example.MediaPlayer;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.PrintWriter;

public class GenerateFiles {
    String[] fileMusic = {
            "<?php",
            "   $arg1 = $_GET['folder'];",
            "   function outputFiles($path){",
            "       if(file_exists($path) && is_dir($path)){",
            "           $result = scandir($path);",
            "           $files = array_diff($result, array('.', '..'));",
            "           if(count($files) > 0){",
            "               foreach($files as $file){",
            "                   if(is_file(\"$path/$file\")){",
            "                       $a = $file;",
            "                       $search = '.mp3';",
            "                       $value = [\".mp3\", \".flac\", \".m4a\", \".wav\", \".wma\"];",
            "                       $count = count($value);",
            "                       for($i = 0; $i < $count; $i++) {",
            "                           if(preg_match(\"/{$value[$i]}/i\", $a)) {",
            "                               echo $file . \"<>\";",
            "                           }",
            "                       }",
            "                   }else if(is_dir(\"$path/$file\")){",
            "                       echo $file . \"<>\";",
            "                   }",
            "               }",
            "           } else{",
            "               echo \"ERROR: No files found in the directory.\";",
            "           }",
            "       } else {",
            "           echo \"ERROR: The directory does not exist.\";",
            "       }",
            "   }",
            "   outputFiles(getcwd() . '/' . $arg1);",
            "?>"
    };

    String[] fileVideo = {
            "<?php",
            "   $arg1 = $_GET['folder'];",
            "   function outputFiles($path){",
            "       if(file_exists($path) && is_dir($path)){",
            "           $result = scandir($path);",
            "           $files = array_diff($result, array('.', '..'));",
            "           if(count($files) > 0){",
            "               foreach($files as $file){",
            "                   if(is_file(\"$path/$file\")){",
            "                       $a = $file;",
            "                       $search = '.mp3';",
            "                       $value = [\".mkv\", \".m4a\", \".mp4\", \".fmp4\", \".webm\", \".mts\", \".m4v\", \".3gp\", \".3g2\", \".flac\", \".flv\",",
            "                               \".vob\", \".gif\", \".avi\", \".mov\", \".qt\", \".wmv\", \".yuv\", \".rm\", \".mpg\", \".mpeg\", \".ogg\"];",
            "                       $count = count($value);",
            "                       for($i = 0; $i < $count; $i++) {",
            "                           if(preg_match(\"/{$value[$i]}/i\", $a)) {",
            "                               echo $file . \"<>\";",
            "                           }",
            "                       }",
            "                   }else if(is_dir(\"$path/$file\")){",
            "                       echo $file . \"<>\";",
            "                   }",
            "               }",
            "           } else{",
            "               echo \"ERROR: No files found in the directory.\";",
            "           }",
            "       } else {",
            "           echo \"ERROR: The directory does not exist.\";",
            "       }",
            "   }",
            "   outputFiles(getcwd() . '/' . $arg1);",
            "   function getSubs($dir){",
            "       $rii = new RecursiveIteratorIterator(new RecursiveDirectoryIterator($dir));",
            "       $files = array();",
            "       foreach ($rii as $file) {",
            "           if ($file->isDir()){",
            "               continue;",
            "           }",
            "           $files[] = $file->getPathname();",
            "       }",
            "       $count = count($files);",
            "       for($i = 0; $i < $count; $i++) {",
            "           $start = strpos($files[$i], getcwd())+strlen(getcwd());",
            "           if(preg_match(\"/.srt/i\", $files[$i])) {",
            "               echo substr($files[$i], $start) . \"<>\";",
            "           }",
            "       }",
            "   }",
            "   getSubs(getcwd() . '/' . $arg1);",
            "?>"
    };

    String[] filePhoto = {
            "<?php",
            "   $arg1 = $_GET['folder'];",
            "   function outputFiles($path){",
            "       if(file_exists($path) && is_dir($path)){",
            "           $result = scandir($path);",
            "           $files = array_diff($result, array('.', '..'));",
            "           if(count($files) > 0){",
            "               foreach($files as $file){",
            "                   if(is_file(\"$path/$file\")){",
            "                       $a = $file;",
            "                       $value = [\".bmp\", \".jpg\", \".jpeg\", \".ico\", \".tiff\", \".gif\", \".png\"];",
            "                       $count = count($value);",
            "                       for($i = 0; $i < $count; $i++) {",
            "                           if(preg_match(\"/{$value[$i]}/i\", $a)) {",
            "                               echo $file . \"<>\";",
            "                           }",
            "                       }",
            "                   }else if(is_dir(\"$path/$file\")){",
            "                       echo $file . \"<>\";",
            "                   }",
            "               }",
            "           } else{",
            "               echo \"ERROR: No files found in the directory.\";",
            "           }",
            "       } else {",
            "           echo \"ERROR: The directory does not exist.\";",
            "       }",
            "   }",
            "   outputFiles(getcwd() . '/' . $arg1);",
            "?>"
    };

    public GenerateFiles(){
        try{
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/music.php");
            PrintWriter pw = new PrintWriter(file);
            for(String s : fileMusic) pw.println(s);
            pw.close();
        }catch (Exception ex){
            Log.d("EXCEPTION: ", "GenerateFiles.GenerateFiles: " + ex.toString());
        }

        try{
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/video.php");
            PrintWriter pw = new PrintWriter(file);
            for(String s : fileVideo) pw.println(s);
            pw.close();
        }catch (Exception ex){
            Log.d("EXCEPTION: ", "GenerateFiles.GenerateFiles: " + ex.toString());
        }

        try{
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/photo.php");
            PrintWriter pw = new PrintWriter(file);
            for(String s : filePhoto) pw.println(s);
            pw.close();
        }catch (Exception ex){
            Log.d("EXCEPTION: ", "GenerateFiles.GenerateFiles: " + ex.toString());
        }

    }


}