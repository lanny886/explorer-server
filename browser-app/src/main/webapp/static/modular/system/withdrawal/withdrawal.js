/**
 * 管理初始化
 */
var withdrawal = {
    id: "withdrawalTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
withdrawal.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: '提现ID', field: 'id', visible: true, align: 'center', valign: 'middle'},
            {title: '提现账号', field: 'ali_account', visible: true, align: 'center', valign: 'middle'},
            {title: '提现人姓名', field: 'ali_name', visible: true, align: 'center', valign: 'middle'},
            {title: '用户ID', field: 'userid', visible: true, align: 'center', valign: 'middle'},
            {title: '提现积分', field: 'score', visible: true, align: 'center', valign: 'middle'},
            {title: '提现状态', field: 'status', visible: true, align: 'center', valign: 'middle',
                formatter: function (value, row, index) {
                      if (value == 0)
                        return '<span class="glyphicon glyphicon-remove label" style="font-size:90%"> 未处理</span>';
                      else if(value == 1)
                        return '<span class="glyphicon glyphicon-ok label label-success" style="font-size:90%"> 成功</span>';
                      else if(value == 2)
                        return '<span class="glyphicon glyphicon-remove label label-danger" style="font-size:90%"> 失败</span>';
                      else
                        return '';
                    }
            },
            {title: '备注', field: 'remarks', visible: true, align: 'center', valign: 'middle'},
            {title: '提现发起时间', field: 'create_time', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
withdrawal.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        withdrawal.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加
 */
withdrawal.openAddWithdrawal = function () {
    var index = layer.open({
        type: 2,
        title: '添加',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/withdrawal/withdrawal_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看详情
 */
withdrawal.openWithdrawalDetail = function () {
    if (this.check()) {
        if(withdrawal.seItem.status != 0){
            Feng.info("该记录已处理！");
                return false;
        }
        var index = layer.open({
            type: 2,
            title: '详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/withdrawal/withdrawal_update/' + withdrawal.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除
 */
withdrawal.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/withdrawal/delete", function (data) {
            Feng.success("删除成功!");
            withdrawal.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("withdrawalId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询列表
 */
withdrawal.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    withdrawal.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = withdrawal.initColumn();
    var table = new BSTable(withdrawal.id, "/withdrawal/list", defaultColunms);
    table.setPaginationType("server");
    withdrawal.table = table.init();
});
