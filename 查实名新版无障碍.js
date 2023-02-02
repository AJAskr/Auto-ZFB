auto("fast");
//定义悬浮窗控制模块，命名为(悬块)。
function 悬块(window, view) {
  //判断是否缺少构造参数。
  if (!window || !view) {
    //缺少构造参数，抛出错误。
    throw "缺参数";
  };
  //记录按键被按下时的触摸坐标
  this.x = 0, this.y = 0;
  //记录按键被按下时的悬浮窗位置
  this.windowX, this.windowY;
  //按下时长超过此值则执行长按等动作
  this.downTime = 500;
  //记录定时执行器的返回id
  this.Timeout = 0;
  //创建点击长按事件
  this.Click = function () { };
  this.LongClick = function () { };
  //可修改点击长按事件
  this.setClick = function (fun) {
    //判断参数类型是否为函数？
    if (typeof fun == "function") {
      this.Click = fun;
    };
  };
  this.setLongClick = function (fun, ji) {
    //判断参数类型是否为函数？
    if (typeof fun == "function") {
      this.LongClick = fun;
      //判断参数是否可为设置数字？
      if (parseInt(ji) <= 1000) {
        this.downTime = parseInt(ji);
      };
    };
  };

  view.setOnTouchListener(new android.view.View.OnTouchListener((view, event) => {
    //判断当前触控事件，以便执行操作。
    switch (event.getAction()) {
      //按下事件。
      case event.ACTION_DOWN:
        //按下记录各种坐标数据。
        this.x = event.getRawX();
        this.y = event.getRawY();
        this.windowX = window.getX();
        this.windowY = window.getY();
        //创建一个定时器用来定时执行长按操作。
        this.Timeout = setTimeout(() => {
          this.LongClick();
          this.Timeout = 0;
        }, this.downTime);
        return true;
      //移动事件。
      case event.ACTION_MOVE:
        //移动距离过大则判断为移动状态
        if (Math.abs(event.getRawY() - this.y) > 5 && Math.abs(event.getRawX() - this.x) > 5) {
          //移动状态清除定时器
          if (this.Timeout) {
            //定时器存在则清除定时器。
            clearTimeout(this.Timeout);
            this.Timeout = 0;
          };
          //移动手指时调整悬浮窗位置
          window.setPosition(this.windowX + (event.getRawX() - this.x), this.windowY + (event.getRawY() - this.y));
        };
        return true;
      //抬起事件。
      case event.ACTION_UP:
        if (this.Timeout) {
          //手指抬起时，定时器存在，说明没有移动和按下时间小于长按时间。
          //清除定时器。
          clearTimeout(this.Timeout);
          this.Timeout = 0;
          //执行点击事件。
          this.Click();
        };
        return true;
    };
    //控件的触控事件函数必须要返回true。否则报错。
    return true;
  }));
};

//上面是悬块,下面开始写悬浮窗
var hoverButton = floaty.rawWindow(
  <frame id='main'>
    <button id='switch' textSize="16sp" w='60' h='60' bg='#09C7F7' text='显示' />
    {/* <button id='switch' textSize="16sp"  w='60' h='60'  bg='@drawable/ic_android_black_48dp'/> */}
  </frame>
)
//输出提示信息。
toastLog("长按悬浮窗关闭本脚本");
//空运行定时器保持脚本运行中,这是悬浮窗脚本所必需的。
setInterval(() => { }, 1000);
// setInterval(() => {移动悬浮按钮至屏幕边缘()}, 500);
//声明一个变量用来控制线程。
var thread = null;
//创建一个新的悬浮控制模块 ad 并带入参数(所要控制的悬浮窗和用来控制悬浮窗移动的控件)。
var ad = new 悬块(hoverButton, hoverButton.switch);
//设置长按事件。
ad.setLongClick(function () {
  //输出气泡信息。
  toast("脚本已关闭");
  //脚本停止代码。
  // console.hide()
  exit();
});
//设置点击事件。
ad.setClick(function () {
  //输出气泡信息。
  // toast("点击");
  //变量值为空则代表线程没有开启。变量值不为空，则判断线程是不是正在运行。
  if (thread ? !thread.isAlive() : true) { //线程没有运行。
    ui.run(() => {
      //在ui线程中修改按钮的文字
      // 显示和隐藏互换
      hoverButton.switch.setText(hoverButton.switch.getText() === '显示' ? '隐藏' : '显示')
    });
    //新建一个线程，赋值给变量thread
    thread = threads.start(function () {
      try {
        Main();
      } catch (e) {
        toastLog(e);
      };
      //运行完毕修改按钮文字
      // ui.run(() => {
      //在ui线程中修改按钮的文字
      // toastLog('点击事件中的方法运行完毕')
      // });
    });
  } else {
    thread.interrupt();
    //中断线程;
    ui.run(() => {
      //在ui线程中修改按钮的文字
      toastLog('点击事件中的方法被中断了')
    });
  };
});
hoverButton.setPosition(device.width - 90, device.height / 2)
setTimeout(
  function () {
    移动悬浮按钮至屏幕边缘()
  }, 500)
function Main() {
  移动悬浮按钮至屏幕边缘()
  侧滑栏toggle()
};
function 移动悬浮按钮至屏幕边缘() {
  // log('移动悬浮按钮至屏幕边缘')

  var 按钮left = hoverButton.getX()
  //移动到左边还是右边,看此时按钮左上角x的大小
  if (按钮left > device.width / 2) {
    移动到屏幕右侧边缘中心()
  } else {
    移动到屏幕左侧边缘中心()
  }
}
function 移动到屏幕右侧边缘中心() {
  var 按钮left = hoverButton.getX()
  var 按钮top = hoverButton.getY()
  var 按钮宽度 = hoverButton.getWidth()
  var 按钮高度 = hoverButton.getHeight()
  // log('按钮left=', 按钮left, '按钮top=', 按钮top)
  // log('按钮宽度=', 按钮宽度, '按钮高度=', 按钮高度)

  var xStart = 按钮left
  var yStart = 按钮top
  var xEnd = device.width - 按钮宽度
  var yEnd = (device.height - 按钮高度) / 2
  // log('xEnd=', xEnd, 'yEnd=', yEnd)

  hoverButton.setPosition(xEnd, yEnd)
}
function 移动到屏幕左侧边缘中心() {
  var 按钮left = hoverButton.getX()
  var 按钮top = hoverButton.getY()
  var 按钮宽度 = hoverButton.getWidth()
  var 按钮高度 = hoverButton.getHeight()
  // log('按钮left=', 按钮left, '按钮top=', 按钮top)
  // log('按钮宽度=', 按钮宽度, '按钮高度=', 按钮高度)

  var xEnd = 0
  var yEnd = (device.height - 按钮高度) / 2
  hoverButton.setPosition(xEnd, yEnd)
}




var sideSlipWindow = floaty.rawWindow(
  <frame>
    <TableLayout id='sideSlip' w='300' h='200' stretchColumns='1' background='#FFFFF0' alpha="0.8">
      <TableRow>
        <button id='RunZfb' textSize="16sp" layout_width='match_parent' layout_height='wrap_content' bg='#09C7F7' text='开始运行' />
      </TableRow>
      <TableRow>
        <EditText id='myid' layout_width='300dp' layout_height='50dp' textSize="15sp" hint='id:' inputType='none'>
        </EditText>
        {/* <text id='Logm' layout_width='300dp' layout_height='250dp' textSize="15sp" text='日志'></text> */}
      </TableRow>
      <TableRow>
        <button id='jbtn' textSize="16sp" layout_width='match_parent' layout_height='wrap_content' marginLeft="3" bg='#ff0000' text='复制ID' />
      </TableRow>
      <TableRow>
        <button id='hide' textSize="16sp" layout_width='match_parent' layout_height='wrap_content' marginLeft="3" bg='#09C7F7' text='隐藏' />
      </TableRow>
    </TableLayout>
  </frame>
)
//设置id框的id
sideSlipWindow.myid.setText('id:' + device.getAndroidId())
//判断是否激活并且改变复制id按钮的文本
//获取语雀里面的值有没有该手机的id
var fg = 0
var yuqueUrl = "https://note.youdao.com/yws/api/note/3c05a5cd3dfaecca974474d0a015c42f?sev=j1&editorType=1&unloginId=cbae0467-d3ed-a829-f694-a03932685fdb&editorVersion=new-json-editor&sec=v1&cstk=AeN4rMwm"
// var yuqueUrl = "www.baidu.com"
http.get(yuqueUrl, {
  headers: {
    'Accept-Language': 'zh-cn,zh;q=0.5',
    'User-Agent': 'Mozilla/5.0(Macintosh;IntelMacOSX10_7_0)AppleWebKit/535.11(KHTML,likeGecko)Chrome/17.0.963.56Safari/535.11'
  }
}, (res, err) => {
  if (err) {
    toast("没有网络~")
    return
  }
  var resText = res.body.string()
  if (resText.indexOf(device.getAndroidId()) != -1) {
    sideSlipWindow.jbtn.setText("已激活")
    fg = 10
  }
})
sideSlipWindow.setPosition(device.width, device.height / 3)
setTimeout(
  function () {
    移动侧滑栏至屏幕边缘第一次()
  }, 500)
function 移动侧滑栏至屏幕边缘第一次() {
  var 按钮left = sideSlipWindow.getX()
  var 按钮top = sideSlipWindow.getY()
  var 按钮宽度 = sideSlipWindow.getWidth()
  var 按钮高度 = sideSlipWindow.getHeight()
  sideSlipWindow.setPosition(device.width, (device.height - 按钮宽度) / 2)
}
function 移动侧滑栏至屏幕边缘() {
  // log('移动侧滑栏至屏幕边缘')
  var 按钮left = sideSlipWindow.getX()
  var 按钮top = sideSlipWindow.getY()
  var 按钮宽度 = sideSlipWindow.getWidth()
  var 按钮高度 = sideSlipWindow.getHeight()

  //移动到左边还是右边,看此时按钮左上角x的大小
  if ((按钮left + 按钮宽度 / 2) > device.width / 2) {
    // log('侧滑栏在屏幕右侧')
    移动侧滑栏到屏幕右侧边缘中心()
  } else {
    // log('侧滑栏在屏幕左侧')
    移动侧滑栏到屏幕左侧边缘中心()
  }
}


function 移动侧滑栏到屏幕右侧边缘中心() {
  // log('移动侧滑栏到屏幕右侧边缘中心')
  var 按钮left = sideSlipWindow.getX()
  var 按钮top = sideSlipWindow.getY()
  var 按钮宽度 = sideSlipWindow.getWidth()
  var 按钮高度 = sideSlipWindow.getHeight()
  // log('按钮left=', 按钮left, '按钮top=', 按钮top)
  // log('按钮宽度=', 按钮宽度, '按钮高度=', 按钮高度)

  var xStart = 按钮left
  var yStart = 按钮top
  var xEnd = device.width
  var yEnd = 按钮top
  // log('xEnd=', xEnd, 'yEnd=', yEnd)
  // log('xEnd-xStart=', xEnd, ' - ', xStart, ' = ', xEnd - xStart)
  for (let i = 0; i < (xEnd - xStart); i = i + 8) {
    sideSlipWindow.setPosition(xStart + i, yEnd)
    // log(xStart + i, yEnd)
    sleep(3)
    if (sideSlipWindow.getX() > device.width) {
      // log('侧边栏左上角横坐标大于屏幕宽度,跳出for循环')
      break
    }
  }
}
function 移动侧滑栏到屏幕左侧边缘中心() {
  // log('移动侧滑栏到屏幕左侧边缘中心')
  var 按钮left = sideSlipWindow.getX()
  var 按钮top = sideSlipWindow.getY()
  var 按钮宽度 = sideSlipWindow.getWidth()
  var 按钮高度 = sideSlipWindow.getHeight()
  // log('按钮left=', 按钮left, '按钮top=', 按钮top)
  // log('按钮宽度=', 按钮宽度, '按钮高度=', 按钮高度)
  var xStart = 按钮left
  var yStart = 按钮top
  var xEnd = -(按钮宽度)
  var yEnd = (device.height - 按钮高度) / 2
  var yUnit = (yEnd - yStart) / (xStart - xEnd)
  var count = 0
  for (let i = 0; i < (xStart - xEnd); i = i + 8) {
    var y = yStart + yUnit * count++;
    sideSlipWindow.setPosition(xStart - i, y)
    sleep(3)
    if (sideSlipWindow.getX() + sideSlipWindow.getWidth() < 0) {
      break
    }
  }
}
var threadSideSlipWindow = null;
var adSideSlipWindow = new 悬块(sideSlipWindow, sideSlipWindow.hide);
//设置长按事件。
// adSideSlipWindow.setLongClick(function() {
//   //输出气泡信息。
//   toast("脚本已关闭");
//   //脚本停止代码。
//   exit();
// });
//设置点击事件。
adSideSlipWindow.setClick(function () {
  //输出气泡信息。
  // toast('点击')
  hoverButton.switch.setText(hoverButton.switch.getText() === '显示' ? '隐藏' : '显示')
  //变量值为空则代表线程没有开启。变量值不为空，则判断线程是不是正在运行。
  if (threadSideSlipWindow ? !threadSideSlipWindow.isAlive() : true) { //线程没有运行。
    // ui.run(() => {
    //在ui线程中修改按钮的文字
    // toastLog('侧滑栏被点击了')
    // });
    //新建一个线程，赋值给变量thread
    threadSideSlipWindow = threads.start(function () {
      try {
        sideSlipWindowMain();
      } catch (e) {
        toastLog(e);
      };
      //运行完毕修改按钮文字
      // ui.run(() => {
      //在ui线程中修改按钮的文字
      // toastLog('侧滑栏点击事件中的方法运行完毕')
      // });
    });
  } else {
    threadSideSlipWindow.interrupt();
    //中断线程;
    ui.run(() => {
      //在ui线程中修改按钮的文字
      toastLog('侧滑栏点击事件中的方法被中断了')
    });
  };
});
//设置复制ID点击事件
var threadSideSlipWindow3 = null;
var adSideSlipWindow3 = new 悬块(sideSlipWindow, sideSlipWindow.jbtn);
adSideSlipWindow3.setClick(function () {
  if (threadSideSlipWindow3 ? !threadSideSlipWindow3.isAlive() : true) {
    ui.run(() => {
      if (fg === 0) {
        setClip(device.getAndroidId())
        toast("复制成功:" + device.getAndroidId())
      } else {
        toast('已激活')
      }
    });
  } else {
    threadSideSlipWindow3.interrupt();
    //中断线程;
    ui.run(() => {
      //在ui线程中修改按钮的文字
      toast('ID出现异常')
    });
  };
});

//设置日志样式
console.show(true)
console.setTitle("日志", "#ff11ee00", 40);
console.setPosition(10, 900);
console.setSize(150, 200);
console.setCanInput(false);

var threadSideSlipWindow2 = null;
var adSideSlipWindow2 = new 悬块(sideSlipWindow, sideSlipWindow.RunZfb);
//设置开始运行点击事件。
adSideSlipWindow2.setClick(function () {
  //变量值为空则代表线程没有开启。变量值不为空，则判断线程是不是正在运行。
  if (threadSideSlipWindow2 ? !threadSideSlipWindow2.isAlive() : true) { //线程没有运行。
    ui.run(() => {
      //在ui线程中修改按钮的文字
      // sideSlipWindow.Logm.setText(sideSlipWindow.Logm.getText()+'111\n111')
      //开始运行
      var mythread = null
      //没有激活不能运行
      if (fg === 10) {
        if (sideSlipWindow.RunZfb.getText() === '开始运行') {
          //不在支付宝的搜索活动不能运行
          var jm = currentActivity().indexOf("globalsearch.ui.MainSearchActivity")
          //不检查界面也跑数据
          if (!files.isFile("/sdcard/支付宝查实名/info.txt")){
          // if (jm === -1 || !files.isFile("/sdcard/支付宝查实名/info.txt")) {
            toast('不能在这里开始运行')
          } else {
            fileJc()
            if (!files.isFile("/sdcard/支付宝查实名/result.txt")) {
              files.create("/sdcard/支付宝查实名/result.txt");
            }
            isrun = true
            sideSlipWindow.RunZfb.setText('停止运行')
            //用线程执行跑数据函数
            mythread = threads.start(() => {
              runJob(sideSlipWindow, mythread, false)
            });
          }
        } else if (sideSlipWindow.RunZfb.getText() === '停止运行') {
          //杀死所有子线程
          threads.shutDownAll()
          fileJc()
          toast('已停止~')
          sideSlipWindow.RunZfb.setText('开始运行')
        }
      } else {
        toast('没有权限使用')
      }
    });
  } else {
    threadSideSlipWindow2.interrupt();
    //中断线程;
    ui.run(() => {
      //在ui线程中修改按钮的文字
      toast('开始运行出现异常')
    });
  };
});

//文件校验
function fileJc() {
  if (files.isFile("/sdcard/支付宝查实名/result.txt") && files.isFile("/sdcard/支付宝查实名/info.txt")) {
    resulttxt = files.read("/sdcard/支付宝查实名/result.txt").split('\n')
    flag = parseInt(resulttxt[resulttxt.length - 2].split(';')[0])
    infotxt = files.read("/sdcard/支付宝查实名/info.txt").split('\n')
    tempdata = []
    for (var i = 0; i < infotxt.length; i++) {
      if (parseInt(infotxt[i].split(';')[0]) > flag) {
        tempdata.push(infotxt[i])
      }
    }
    files.write('/sdcard/支付宝查实名/info.txt', '')
    for (var i = 0; i < tempdata.length; i++) {
      files.append('/sdcard/支付宝查实名/info.txt', tempdata[i] + '\n')
    }
    resulttxt = null
    flag = null
    infotxt = null
    tempdata = null
  }
}

// 修复result.txt文件 （把跑数据因为线程异常而确实的数据找个假数据补上）
function fileRepair(){
  if(files.isFile("/sdcard/支付宝查实名/result.txt")){
    flag = false
    resulttxt = files.read("/sdcard/支付宝查实名/result.txt").split('\n')
    for (var i=0;i<resulttxt.length-2;i++){
      if(resulttxt[i+1].length>1){
        if(parseInt(resulttxt[i].split(';')[0])+1!==parseInt(resulttxt[i+1].split(';')[0])){
          falg = true
          resulttxt.splice(i+1,0,(parseInt(resulttxt[i].split(';')[0])+1)+';替补数据;12212212233;false')
        }
      }
    }
    files.write('/sdcard/支付宝查实名/result.txt', '')
    for(var i=0;i<resulttxt.length;i++){
      if(resulttxt[i].length>1){
        files.append('/sdcard/支付宝查实名/result.txt', resulttxt[i] + '\n')
      }
    }
    if(flag){
      toast('修复成功')
    }else{
      toast('数据检查正常')
    }
  }
}

//开始跑数据
function runJob(sideSlipWindow, mythread, exitmythread) {
  //跑数据的规则
  isrun = true
  infotxt = files.read("/sdcard/支付宝查实名/info.txt").split('\n')
  for (var i = 0; i < infotxt.length; i++) {
    //如果isrun为false就退出循环
    if (!isrun) break
    phonesLen = parseInt(infotxt[i].split(';').length) - 3
    flag2 = false
    //查找的状态
    mystatus = '没找到'
    for (var j = 0; j < phonesLen; j++) {
      //如果法人和搜索到的匹配成功就退出
      if (flag2) break
      //设置文本框内容为法人手机号
      setText(infotxt[i].split(';')[j + 2])
      //没找到之前为true
      findthis = true
      //法人的名字
      frname = infotxt[i].split(';')[1]
      //搜索出来的名字
      searchName = ''
      //超时的起始时间 不能超过10s
      oldTime = new Date().getTime()
      while (findthis) {
        //超时的结束时间
        nowTime = new Date().getTime()
        if (parseInt(nowTime - oldTime) > 1000) {
          mystatus = '超时'
          log(mythread)
          findthis = false
          exitmythread = true
          break
        }
        rp = className("android.widget.TextView").find()
        //rp.text()的循环控制
        flag3 = false
        for (var k = 0; k < rp.length; k++) {
          //符合了情况这里就break了
          if (flag3) break
          var rptext = rp[k].text()
          if (rptext === '') continue
          if (rptext === '找找其他的吧') {
            mystatus = '没找到'
            findthis = false
            break
          } else if (rptext === '向右滑动验证') {
            //验证就输出需要验证，按钮变成开始运行
            sideSlipWindow.RunZfb.setText('开始运行')
            mystatus = '验证'
            log(mystatus + ':请手动滑动验证')
            findthis = false
            flag2 = true
            isrun = false
            break
          } else if (rptext === '网络有点忙') {
            //网络有点忙
            sideSlipWindow.RunZfb.setText('开始运行')
            mystatus = '网络有点忙'
            log(mystatus + ':如果点刷新也不行\n那么这个账号可能频繁了')
            findthis = false
            flag2 = true
            isrun = false
            break
          } else if (rptext === '您请求次数过于频繁') {
            //频繁
            sideSlipWindow.RunZfb.setText('开始运行')
            mystatus = '频繁'
            log(mystatus + ':该支付宝账号频繁，请换一个账号继续')
            findthis = false
            flag2 = true
            isrun = false
            break
          }
          for (var op = 0; op < rptext.length; op++) {
            if (rptext[op] === '(') {
              // 检查法人名称和查到的是否校验成功
              for (var yp = rptext.indexOf('(') + 1; yp < rptext.indexOf(')'); yp++) {
                searchName += rptext[yp]
              }
              //如果匹配成功
              if (searchName !== '未实名' && searchName.length === frname.length && searchName[searchName.length - 1] === frname[frname.length - 1]) {
                //控制台输出日志
                log(infotxt[i] + j + '\n')
                //result.txt写入该记录
                files.append('/sdcard/支付宝查实名/result.txt', infotxt[i] + j + '\n')
                //找到直接退出四层的循环
                findthis = false
                flag3 = true
                flag2 = true
                break
              } else {
                //匹配不成功状态就是没找到 找该法人的下一个手机号
                mystatus = '没找到'
                findthis = false
                flag3 = true
                break
              }
            }
          }
        }
      }
    }
    //如果该法人的所有手机号都没找到的情况
    if (mystatus === '没找到' && !flag2) {
      //result.txt写入该记录
      if (infotxt[i].length > 1 && infotxt[i]!=='') {
        //控制台输出日志
        log(infotxt[i] + 'false\n')
        files.append('/sdcard/支付宝查实名/result.txt', infotxt[i] + 'false' + '\n')
      }
    }
    //如果最后一个执行完毕 那么退出线程
    if (i + 1 === infotxt.length) {
      log('执行完毕')
      fileRepair()
      exitmythread = true
    }
    // exitmythread = true
    // break
  }
  //所有数据都跑完那么就关闭线程 按钮变成开始运行
  if (exitmythread) {
    sideSlipWindow.RunZfb.setText('开始运行')
    mythread.interrupt()
  }
}

function sideSlipWindowMain() {
  // log('侧滑栏toggle')
  侧滑栏toggle()
}
function 侧滑栏toggle() {
  // log('侧滑栏toggle')
  if (侧滑栏中心点是否在屏幕中()) {
    // log('侧滑栏中心点在屏幕中')
    // log('移动侧滑栏至屏幕边缘()开始')
    移动侧滑栏至屏幕边缘()
    // log('移动侧滑栏至屏幕边缘()结束')
  } else {
    // log('侧滑栏中心点不在屏幕中')
    移动侧滑栏至屏幕中()
  }
}
function 移动侧滑栏至屏幕中() {
  var 侧滑栏中心点 = 获取侧滑栏中心点坐标()
  // log('侧滑栏中心点', 侧滑栏中心点)
  var x = 侧滑栏中心点.x
  var y = 侧滑栏中心点.y
  if (x > device.width / 2) {
    // log('移动侧滑栏到屏幕右侧')
    移动侧滑栏到屏幕右侧()
  } else {
    // log('移动侧滑栏到屏幕左侧')
    移动侧滑栏到屏幕左侧()
  }
}
function 移动侧滑栏到屏幕右侧() {
  // log('移动侧滑栏到屏幕右侧')
  var 按钮left = sideSlipWindow.getX()
  var 按钮top = sideSlipWindow.getY()
  var 按钮宽度 = sideSlipWindow.getWidth()
  var 按钮高度 = sideSlipWindow.getHeight()
  // log('按钮left=', 按钮left, '按钮top=', 按钮top)
  // log('按钮宽度=', 按钮宽度, '按钮高度=', 按钮高度)

  var xStart = 按钮left
  var yStart = 按钮top
  var xEnd = device.width - 按钮宽度
  var yEnd = 按钮top
  // log('xEnd=', xEnd, 'yEnd=', yEnd)
  for (let i = 0; i < (xStart - xEnd); i = i + 6) {
    sideSlipWindow.setPosition(xStart - i, yEnd)
    // log(xStart - i, yEnd)
    sleep(3)
    if (sideSlipWindow.getX() < device.width - 按钮宽度) {
      // log('侧滑栏移动到屏幕中了,跳出for循环')
      break
    }
  }
}
function 移动侧滑栏到屏幕左侧() {
  // log('移动侧滑栏到屏幕左侧')
  var 按钮left = sideSlipWindow.getX()
  var 按钮top = sideSlipWindow.getY()
  var 按钮宽度 = sideSlipWindow.getWidth()
  var 按钮高度 = sideSlipWindow.getHeight()
  // log('按钮left=', 按钮left, '按钮top=', 按钮top)
  // log('按钮宽度=', 按钮宽度, '按钮高度=', 按钮高度)

  var xStart = 按钮left
  var yStart = 按钮top
  var xEnd = 0
  var yEnd = (device.height - 按钮高度) / 2
  var yUnit = (yEnd - yStart) / (xEnd - xStart)
  var count = 0
  // log('xEnd-xStart=', xEnd - xStart)
  for (let i = 0; i < (xEnd - xStart); i = i + 8) {
    var y = yStart + yUnit * count++;
    sideSlipWindow.setPosition(xStart + i, y)
    sleep(3)
    if (sideSlipWindow.getX() > 0) {
      // log('sideSlipWindow.getX()>0,跳出for循环')
      break
    }
  }











}



function 侧滑栏中心点是否在屏幕中() {
  var 侧滑栏中心点 = 获取侧滑栏中心点坐标()
  // log('侧滑栏中心点=', 侧滑栏中心点)
  var x = 侧滑栏中心点.x
  var y = 侧滑栏中心点.y
  if (x > 0 && x < device.width && y > 0 && y < device.height) {
    return true
  }
  return false
}
function 获取侧滑栏中心点坐标() {
  var x = sideSlipWindow.x
  var y = sideSlipWindow.y
  var width = sideSlipWindow.getWidth()
  var height = sideSlipWindow.getHeight()
  var centerX = (x + width / 2)
  var centerY = (y + height / 2)
  var centerXY = {
    x: centerX,
    y: centerY
  }
  return centerXY
}
