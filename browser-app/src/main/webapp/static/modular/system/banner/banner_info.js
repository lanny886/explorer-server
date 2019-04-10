/**
 * 初始化详情对话框
 */


var BannerInfoDlg = {
    bannerInfoData : {}
};

/**
 * 清除数据
 */
BannerInfoDlg.clearData = function() {
    this.bannerInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
BannerInfoDlg.set = function(key, val) {
    this.bannerInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

    //图片显示
    function img(){
        var hmc=$("#hmc");
        var title="图片显示"
        showSimpleDialog(hmc,title,'auto','auto');
    }

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
BannerInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
BannerInfoDlg.close = function() {
    parent.layer.close(window.parent.Banner.layerIndex);
}

/**
 * 收集数据
 */
BannerInfoDlg.collectData = function() {
    this
    .set('id')
    .set('url')
    .set('imageUrl')
    .set('img');
}

/**
 * 提交添加
 */
BannerInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    var input = document.getElementById("img");
    var file = document.querySelector('input[type=file]').files[0];

    if(file == undefined){
        var ajax = new $ax(Feng.ctxPath + "/banner/add", function(data){
            Feng.success("添加成功!");
            window.parent.Banner.table.refresh();
            BannerInfoDlg.close();
        },function(data){
            Feng.error("添加失败!" + data.responseJSON.message + "!");
        });
        ajax.set(BannerInfoDlg.bannerInfoData);
        ajax.start();
    }else{
         var reader = new FileReader();
         reader.readAsDataURL(file);

         reader.onload = function(e){
             //将编码后的图片字符串放在输入框中
             $("#imageUrl").val(this.result);

            //提交信息
            var ajax = new $ax(Feng.ctxPath + "/banner/add", function(data){
                Feng.success("添加成功!");
                window.parent.Banner.table.refresh();
                BannerInfoDlg.close();
            },function(data){
                Feng.error("添加失败!" + data.responseJSON.message + "!");
            });
            BannerInfoDlg.set("imageUrl",$("#imageUrl").val());
            ajax.set(BannerInfoDlg.bannerInfoData);
            ajax.start();

         }
         $("#imageUrl").val("");
     }
}

/**
 * 提交修改
 */
BannerInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    var input = document.getElementById("img");
    var file = document.querySelector('input[type=file]').files[0];

    if(file == undefined){

        //提交信息
        var ajax = new $ax(Feng.ctxPath + "/banner/update", function(data){
            Feng.success("修改成功!");
            window.parent.Banner.table.refresh();
            BannerInfoDlg.close();
        },function(data){
            Feng.error("修改失败!" + data.responseJSON.message + "!");
        });
        ajax.set(this.bannerInfoData);
        ajax.start();

     }else{
           var reader = new FileReader();
           reader.readAsDataURL(file);

           reader.onload = function(e){
               //将编码后的图片字符串放在输入框中
               $("#imageUrl").val(this.result);

              //提交信息
              var ajax = new $ax(Feng.ctxPath + "/banner/add", function(data){
                  Feng.success("添加成功!");
                  window.parent.Banner.table.refresh();
                  BannerInfoDlg.close();
              },function(data){
                  Feng.error("添加失败!" + data.responseJSON.message + "!");
              });
              BannerInfoDlg.set("imageUrl",$("#imageUrl").val());
              ajax.set(BannerInfoDlg.bannerInfoData);
              ajax.start();
           }
           $("#imageUrl").val("");
      }
}

$(function() {

});

