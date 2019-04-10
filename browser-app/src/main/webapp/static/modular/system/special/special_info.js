/**
 * 初始化详情对话框
 */
var specialInfoDlg = {
    specialInfoData : {}
};

/**
 * 清除数据
 */
specialInfoDlg.clearData = function() {
    this.specialInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
specialInfoDlg.set = function(key, val) {
    this.specialInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
specialInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
specialInfoDlg.close = function() {
    parent.layer.close(window.parent.special.layerIndex);
}

/**
 * 收集数据
 */
specialInfoDlg.collectData = function() {
    this
    .set('id')
    .set('imageUrl')
    .set('url')
    .set('title')
    .set('seq')
    .set('qrShow')
    .set('qrInfo');
}

/**
 * 提交添加
 */
specialInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();


    var input = document.getElementById("img");
    var file = document.querySelector('input[type=file]').files[0];

    if(file == undefined){
        //提交信息
        var ajax = new $ax(Feng.ctxPath + "/special/add", function(data){
            Feng.success("添加成功!");
            window.parent.special.table.refresh();
            specialInfoDlg.close();
        },function(data){
            Feng.error("添加失败!" + data.responseJSON.message + "!");
        });
        ajax.set(this.specialInfoData);
        ajax.start();
    }else{
          var reader = new FileReader();
          reader.readAsDataURL(file);

          reader.onload = function(e){
              //将编码后的图片字符串放在输入框中
              $("#imageUrl").val(this.result);

             //提交信息
             var ajax = new $ax(Feng.ctxPath + "/special/add", function(data){
                 Feng.success("添加成功!");
                 window.parent.special.table.refresh();
                 specialInfoDlg.close();
             },function(data){
                 Feng.error("添加失败!" + data.responseJSON.message + "!");
             });
             specialInfoDlg.set("imageUrl",$("#imageUrl").val());
             ajax.set(specialInfoDlg.specialInfoData);
             ajax.start();

          }
          $("#imageUrl").val("");
      }
}

/**
 * 提交修改
 */
specialInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    var input = document.getElementById("img");
    var file = document.querySelector('input[type=file]').files[0];

    if(file == undefined){
        //提交信息
        var ajax = new $ax(Feng.ctxPath + "/special/update", function(data){
            Feng.success("修改成功!");
            window.parent.special.table.refresh();
            specialInfoDlg.close();
        },function(data){
            Feng.error("修改失败!" + data.responseJSON.message + "!");
        });
        ajax.set(this.specialInfoData);
        ajax.start();
    }else{
        var reader = new FileReader();
        reader.readAsDataURL(file);

        reader.onload = function(e){
            //将编码后的图片字符串放在输入框中
            $("#imageUrl").val(this.result);

           //提交信息
           var ajax = new $ax(Feng.ctxPath + "/special/add", function(data){
               Feng.success("添加成功!");
               window.parent.Banner.table.refresh();
               specialInfoDlg.close();
           },function(data){
               Feng.error("添加失败!" + data.responseJSON.message + "!");
           });
           specialInfoDlg.set("imageUrl",$("#imageUrl").val());
           ajax.set(specialInfoDlg.specialInfoData);
           ajax.start();
        }
        $("#imageUrl").val("");
   }
}

$(function() {

});
