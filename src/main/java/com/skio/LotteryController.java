package com.skio;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * Created by Jason on 15-1-8.
 */
@Controller
public class LotteryController {
    private final String ONE = "1";
    private final String TWO = "2";
    private final String THREE = "3";
    private final String NORMALNUM = "9";
    private Map<String,List<String>> returnList;
    private List<String> returnNumList;
    private String ONETOP = "3";
    private String TWOTOP = "5";
    private String THREETOP = "0";
    private String TOTAlNUM = "350";

    @ResponseBody
    @RequestMapping(value = "/initNum/{num}")
    public String initLotteryNum(HttpServletRequest request, @PathVariable(value = "num") Integer num) {
        if(num != 0) {
            TOTAlNUM = num+"";
            ONETOP = TOTAlNUM.substring(0, 1);
            TWOTOP = TOTAlNUM.substring(1, 2);
            THREETOP = TOTAlNUM.substring(2, 3);
            String path = request.getSession().getServletContext().getRealPath("/static/winner");
            request.getSession().setAttribute("lotteryList",null);
            request.getSession().setAttribute("lotteryNumList",null);
            try {
                File file = new File(path + "/winner.txt");
                FileUtils.cleanDirectory(new File(path));
                file.createNewFile();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "系统初始化成功";
        }
        return "系统初始化失败";
    }

    /**
     * 抽奖二
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/people")
    public Map<String,List<String>> people(HttpServletRequest request) {
        if (request.getSession().getAttribute("lotteryList") == null) {
            returnList = lotterInitList(request);
            request.getSession().setAttribute("lotteryList", returnList);
            return returnList;
        } else {
            returnList = (Map<String,List<String>>) request.getSession().getAttribute("lotteryList");
            //打乱数组
            returnList.put(ONE,randomList(returnList.get(ONE)));
            returnList.put(TWO,randomList(returnList.get(TWO)));
            returnList.put(THREE,randomList(returnList.get(THREE)));
            request.getSession().setAttribute("lotteryList", returnList);
            return returnList;
        }
    }

    /**
     * 抽奖一
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/peopleNum")
    public List<String> peopleNum(HttpServletRequest request) {
        if (request.getSession().getAttribute("lotteryNumList") == null) {
            returnNumList = lotterInitNumList(request);
            request.getSession().setAttribute("lotteryNumList", returnNumList);
            return returnNumList;
        } else {
            returnNumList = (List<String>) request.getSession().getAttribute("lotteryNumList");
            //移除已经冲将人员名单
            List<String> winnerlist = winner(request);
            for (String s : winnerlist) {
                if (returnNumList.contains(s)) {
                    returnNumList.remove(s);
                }
            }
            //打乱数组
            returnNumList = randomList(returnNumList);
            request.getSession().setAttribute("lotteryNumList", returnNumList);
            return returnNumList;
        }
    }
    private List<String> lotterInitNumList(HttpServletRequest request) {
         returnNumList = randomList(creatNum(Integer.valueOf(TOTAlNUM)));
         List<String> winnerlist = winner(request);
         for (String s : winnerlist) {
             if (returnNumList.contains(s)) {
                  returnNumList.remove(s);
             }
         }
         return returnNumList;
    }

    @ResponseBody
    @RequestMapping(value = "/lotteryNum/{index}")
     public String lotteryNum(HttpServletRequest request, @PathVariable(value = "index") Integer index) {
        returnNumList = (List<String>) request.getSession().getAttribute("lotteryNumList");
        String num = returnNumList.get(index > returnNumList.size() ? returnNumList.size() : index);
        write(request,num);
        return num;
    }

    @ResponseBody
    @RequestMapping(value = "/lottery/{index}")
    public String lottery(HttpServletRequest request, @PathVariable(value = "index") Integer index, String step, @RequestParam(value = "numThree",required = false) String numThree,@RequestParam(value = "numTwo",required = false) String numTwo) {
        returnList = (Map<String,List<String>>) request.getSession().getAttribute("lotteryList");
        if(THREE.equals(step)){
            String three = returnList.get(THREE).get(index);
            return three;
        }else if (TWO.equals(step)){
            String two = returnList.get(TWO).get(index);
            while (!checkTwo(request,index,two,numThree)){
                index = index - 1 < 0 ? returnList.get(TWO).size() - 1 : index - 1;
                two = returnList.get(TWO).get(index);

            }
            return two;
        }else {
            List<String> oneList = new ArrayList<String>();
            int numFromIndex =  0;
            if(numThree != null && numTwo != null) {
                numFromIndex = Integer.valueOf(numTwo + numThree);
            }
            //个，十 确定，控制百位大小
            int numMax = Integer.valueOf(TWOTOP + THREETOP);
            if(numFromIndex > numMax) {
                oneList = creatNum(Integer.valueOf(ONETOP)-1);
            }else{
                oneList = creatNum(Integer.valueOf(ONETOP));
            }
            //移除已经冲将人员名单
            List<String> winnerlist = winner(request);
            for (String s : winnerlist) {
                if(s.length() > 0){
                    String stb = s.substring(s.length()-2,s.length());
                    if(stb.equals(numTwo + numThree)){
                        String stbOne = s.substring(0,1);
                        oneList.remove(stbOne);
                    }
                }
            }
            returnList.put(ONE,randomList(oneList));
            String one = "";
            int size=returnList.get(ONE).size()-1;
            one = returnList.get(ONE).get(index > size ? size:index);
            write(request,one + numTwo + numThree);
            lotterInitList(request);
            return one;
        }
    }
    public boolean checkTwo(HttpServletRequest request,int index, String two,String three){
        //移除已经冲将人员名单
        List<String> winnerlist = winner(request);
        int on = 0;
        for (String s : winnerlist) {
            if(s.length() > 0){
                String stb = s.substring(s.length()-2,s.length());
                if(stb.equals(two + three)){
                    on = on +1;
                }
            }
        }
        if(on >= Integer.valueOf(ONETOP)){
            return false;
        }
        return true;
    }
    @ResponseBody
    @RequestMapping(value = "/winner")
    public List<String> winner(HttpServletRequest request) {
        String path = request.getSession().getServletContext().getRealPath("/static/winner");
        List<String> list = new ArrayList<String>();
        try {
            File file = new File(path + "/winner.txt");
            list = FileUtils.readLines(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 写入winner。txt
     * @param request
     * @param name
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/write", method = RequestMethod.POST)
    public String write(HttpServletRequest request, String name) {
        String path = request.getSession().getServletContext().getRealPath("/static/winner");
        try {
            File file = new File(path + "/winner.txt");
            FileUtils.writeStringToFile(file, name, true);
            FileUtils.writeStringToFile(file, "\n", true);
            return "s";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "f";
    }

    /**
     * 抽奖二 ：数字一个一个停
     * 所有参与抽奖数据
     * @param request
     * @return
     */
    private Map<String,List<String>> lotterInitList(HttpServletRequest request) {
        Map<String,List<String>> map = new HashMap<String,List<String>>();
        map.put(ONE,randomList(creatNum(Integer.valueOf(ONETOP))));
        map.put(TWO,randomList(creatNum(Integer.valueOf(NORMALNUM))));
        map.put(THREE,randomList(creatNum(Integer.valueOf(NORMALNUM))));
        return map;
    }

    public List<String> creatNum(int num){
        List<String> list = new ArrayList<String>();
        if(num > 100){
            for (int i = 0; i < 100 ; i++) {
                list.add((i + "").length()==1?"00"+i:"0"+i);
            }
            for (int j = 100; j < num ; j++){
                list.add(j + "");
            }
        }else{
            for (int i = 0; i <= num; i++) {
                list.add(i + "");
            }
        }

        return list;
    }

    /**
     * 打乱数组
     * @param list
     * @return
     */
    public List<String> randomList(List<String> list){
        List<String> randomList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            int j = new Random().nextInt(list.size());
            String temp = list.get(j);
            randomList.add(temp);
            list.remove(temp);
            i--;
        }
        return randomList;
    }
}
