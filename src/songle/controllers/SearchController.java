package songle.controllers;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import songle.engine.IndexHelper;
import songle.engine.Result;
import songle.engine.VSM;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Carlos on 4/15/2017.
 */
@Controller
public class SearchController {
    @Autowired
    ServletContext context;

//    @RequestMapping(value = "search", produces="application/json")
    @RequestMapping(value="search", method= RequestMethod.POST)
    @ResponseBody
    public String welcomeHandler(ModelMap model, WebRequest request) {

//        String path = context.getRealPath("data/testdata.csv");
//        File a = new File("./");
//        File parentFolder = new File(a.getParent());
//        System.out.println("Absolute PAth:: "+path);
        VSM vsm = new VSM(IndexHelper.indexPath);
        HashMap<String, Object> resultSet = new HashMap<>();
//        List<Result> foundLyrics = vsm.rankSearch(request.getParameter("query"));
        List<Result> foundLyrics = vsm.search(request.getParameter("query"), 0, 20);
        foundLyrics = foundLyrics.size() > 50 ? foundLyrics.subList(0, 50) : foundLyrics;

//        for (Result res: foundLyrics) {
//            res.lyrics = IndexHelper.loadText(IndexHelper.indexPath+"/lyrics/"+res.songId+".txt");
//        }
        resultSet.put("results", foundLyrics);
        resultSet.put("results_count", vsm.fullSearchSize);
        resultSet.put("suggestion", vsm.getQuerySuggestion());
//        List<Result> results = ;
//        String querySuggestion = ;
        //System.out.println("RESULTS: " + results.size());
//        for (Result res: results) {
//            System.out.println(res.title);
//        }
//        model.addAttribute("message", "Hello Spring MVC Framework!");
//       model.addAttribute("results", results.subList(0, 10));
//        JSON.stringify
//        String jsonText = JSONValue.toJSONString(map);

        String json = new Gson().toJson(resultSet);
        return json;
    }
}
