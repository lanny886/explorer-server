/**
 * 初始化详情对话框
 */
var withdrawalInfoDlg = {
    withdrawalInfoData : {}
};

/**
 * 清除数据
 */
withdrawalInfoDlg.clearData = function() {
    this.withdrawalInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
withdrawalInfoDlg.set = function(key, val) {
    this.withdrawalInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
withdrawalInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
withdrawalInfoDlg.close = function() {
    parent.layer.close(window.parent.withdrawal.layerIndex);
}

/**
 * 收集数据
 */
withdrawalInfoDlg.collectData = function() {
    this
    .set('id')
    .set('aliAccount')
    .set('aliName')
    .set('userid')
    .set('score')
    .set('status')
    .set('createTime')
    .set('updateTime')
    .set('t')
    .set('code');
}

/**
 * 提交添加
 */
withdrawalInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/withdrawal/add", function(data){
        Feng.success("添加成功!");
        window.parent.withdrawal.table.refresh();
        withdrawalInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.withdrawalInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
withdrawalInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/withdrawal/update", function(data){
        Feng.success("修改成功!");
        window.parent.withdrawal.table.refresh();
        withdrawalInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.withdrawalInfoData);
    ajax.start();
}

$(function() {

});
