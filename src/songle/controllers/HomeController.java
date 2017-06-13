package songle.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import songle.engine.IndexHelper;
import songle.engine.Result;
import songle.engine.VSM;

import javax.print.Doc;
import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

@Controller
public class HomeController {



    @RequestMapping("")
    public String welcomeHandler(ModelMap model) {
        model.addAttribute("message", "Hello Spring MVC Framework! ");
        VSM vsm = new VSM(IndexHelper.indexPath);
        model.addAttribute("clusters", vsm.getClusters());
        return "index";
    }

    @RequestMapping(value = "clusters", method = RequestMethod.GET)
    public String clusterDocs(ModelMap model, WebRequest request){
        int clusterId = Integer.parseInt(request.getParameter("cluster_id"));
        VSM vsm = new VSM(IndexHelper.indexPath);
        List<Result> lyrics = vsm.getClusterDocs(clusterId, 0, 100);
        model.addAttribute("lyrics", lyrics);

        return "clusters";
    }

    @RequestMapping(value = "lyrics", method = RequestMethod.GET)
    public String lyrics(ModelMap model, WebRequest request){
        String lyrics = "";

        int songId = Integer.parseInt(request.getParameter("song_id"));
//        File lyricsFile = ;

        try {
//            FileReader fileReader = new FileReader(IndexHelper.indexPath+"/lyrics/"+songId+".txt");
            Scanner scan = new Scanner(new File(IndexHelper.indexPath+"/lyrics/"+songId+".txt"));
            while(scan.hasNextLine()){
                lyrics += scan.nextLine().toLowerCase()+"<br />";
            }

            scan.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        VSM vsm = new VSM(IndexHelper.indexPath);
//        List<Result> similar = vsm.getSimilar(lyrics.replace("<br />", " "));
        List<Result> similar = vsm.rankSearch(lyrics.replace("<br />", " "));

        Comparator<Result> c = new Comparator<Result>() {
            public int compare(Result d1, Result d2) {
                return new Integer(d1.songId).compareTo(d2.songId);
            }
        };

        int index = Collections.binarySearch(similar, new Result(0, songId), c);
        if(index==0) similar.remove(index);

        similar = similar.size() > 5 ? similar.subList(0,5) : similar;

        model.addAttribute("lyricsText", lyrics);
        model.addAttribute("similar", similar);
        return "lyrics";
    }

//    @RequestMapping(value = "message", method = RequestMethod.GET)
//    public String messages(Model model) {
////        model.addAttribute("messages", messageRepository.findAll());
//        return "message/list";
//    }

    @RequestMapping(value = "message", method = RequestMethod.GET)
    public String messages(Model model) {
        model.addAttribute("messages", "TEST");
        return "index2";
    }

//    @RequestMapping("/vets")
//    public ModelMap vetsHandler() {
//        return new ModelMap(this.clinic.getVets());
//    }

}