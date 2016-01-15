/**
 * Created by Administrator on 15-1-15.
 */
var f = 0;
var ff = 1;
var ff0 = 1;
var ff1 = 1;
var ff2 = 1;
var ff3 = 1;
var sound1 = new Audio("static/music/run.m4r");
sound1.load();
var num0;
var num1;
var num2;
var num3;
$.get("/peopleNum",function(data){
    num0 = data;
});
$.get("/people", function (data) {
    num1 = data["1"];
    num2 = data["2"];
    num3 = data["3"];
});
var showChosen0 = eval(Wind.compile("async", function (idx) {
    $(".name #b0").text(idx);
}));
var showChosen1 = eval(Wind.compile("async", function (idx) {
    $(".name #b1").text(idx);
}));
var showChosen2 = eval(Wind.compile("async", function (idx) {
    $(".name #b2").text(idx);
}));
var showChosen3 = eval(Wind.compile("async", function (idx) {
    $(".name #b3").text(idx);
}));
var animate0 = eval(Wind.compile("async", function (idx) {
    $await(showChosen0(idx));
}));
var animate1 = eval(Wind.compile("async", function (idx) {
    $await(showChosen1(idx));
}));
var animate2 = eval(Wind.compile("async", function (idx) {
    $await(showChosen2(idx));
}));
var animate3 = eval(Wind.compile("async", function (idx) {
    $await(showChosen3(idx));
}));
var no0 = 0;
var no1 = 0;
var no2 = 0;
var no3 = 0;
var numThree = 0;
var numTwo = 0;
var heartBeat0 = eval(Wind.compile("async", function () {
    ff = 0;
    while (true) {
        if(f==2){
            break;
        }
        if (num0.length <= 0) {
            break;
        }
        if (ff0 == 1) {
            sound1.pause();
            sound1.currentTime = 0.0;
            $.get("/lotteryNum/" + no0, function (data) {
                $(".name #b0").text(data);
            });
            $.get("/peopleNum",function(data){
                num0 = data;
            });
            break;
        }
        try {
            sound1.play();
        } catch (e) {
        }

        var l = num0.length;
        no0 = Math.floor(Math.random() * l);
        $await(animate0(no0));
        $await(Wind.Async.sleep(20));
    }
}));
var heartBeat1 = eval(Wind.compile("async", function () {
    ff = 0;
    while (true) {
        if(f==1){
            break;
        }
        if (num1.length <= 0) {
            break;
        }
        if(ff1 == 1){
            sound1.pause();
            sound1.currentTime = 0.0;
            $.get("/lottery/" + no1,{"step":"1","numThree":numThree,"numTwo":numTwo}, function (data) {
                $(".name #b1").text(data);
                numOne=data;
            });
            ff = 1;
            break;
        }

        try {
            sound1.play();
        } catch (e) {
        }
        var l1 = num1.length;
        no1 = Math.floor(Math.random() * l1);
        $await(animate1(no1));
        $await(Wind.Async.sleep(20));
    }
}));
var heartBeat2 = eval(Wind.compile("async", function () {
    ff = 0;
    while (true) {
        if(f==1){
            break;
        }
        if(num2.length <= 0){
            break;
        }
        if(ff2 == 1){
            $.get("/lottery/" + no2,{"step":"2","numThree":numThree}, function (data) {
                $(".name #b2").text(data);
                numTwo=data;
            });
            break;
        }
        try {
            sound1.play();
        } catch (e) {
        }

        var l2 = num2.length;
        no2 = Math.floor(Math.random() * l2);
        $await(animate2(no2));
        $await(Wind.Async.sleep(20));
    }
}));

var heartBeat3 = eval(Wind.compile("async", function () {
    ff = 0;
    while (true) {
        if(f==1){
            break;
        }
        if(num3.length <= 0){
            break;
        }
        if(ff3 == 1){
            $.get("/lottery/" + no3,{"step":"3"}, function (data) {
                $(".name #b3").text(data);
                numThree=data;
            });
            break;
        }
        try {
            sound1.play();
        } catch (e) {
        }
        var l3 = num3.length;
        no3 = Math.floor(Math.random() * l3);
        $await(animate3(no3));
        $await(Wind.Async.sleep(20));
    }
}));


$(window).keydown(function (event) {
    switch (event.keyCode) {
        //回车 抽奖开始
        case 13:
            if (ff == 0) {
                break;
            }
            f = 0;
            ff = 0;
            ff0 = 0;
            ff1 = 0;
            ff2 = 0;
            ff3 = 0;
            heartBeat0().start();
            heartBeat1().start();
            heartBeat2().start();
            heartBeat3().start();
            break;
        //空格 抽奖结束
        case 32:
            f = 1;
            $("#lotteryOne").show();
            $("#lotteryTwo").hide();
            ff = 1;
            ff0 = 1;
            break;
        //1
        case 49:
        case 97:
            if (ff1 == 1) {
                return;
            }
            ff1 = 1;
            ff2 = 0;
            ff3 = 0;
            f = 2;
            break;
        //2
        case 50:
        case 98:
            if (ff2 == 1) {
                return;
            }
            ff1 = 0;
            ff2 = 1;
            ff3 = 0;
            f = 2;
            break;
        //3
        case 51:
        case 99:
            if (ff3 == 1) {
                return;
            }
            $("#lotteryOne").hide();
            $("#lotteryTwo").show();
            f = 2;
            ff1 = 0;
            ff2 = 0;
            ff3 = 1;
            break;
    }
});