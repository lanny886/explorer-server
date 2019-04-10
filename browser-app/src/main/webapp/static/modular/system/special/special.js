/**
 * 管理初始化
 */
var special = {
    id: "specialTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
special.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: '专题ID', field: 'id', visible: true, align: 'center', valign: 'middle'},
            {title: '专题图片', field: 'imageUrl', visible: true, align: 'center', valign: 'middle'},
            {title: '专题链接', field: 'url', visible: true, align: 'center', valign: 'middle'},
            {title: '专题标题', field: 'title', visible: true, align: 'center', valign: 'middle'},
            {title: '专题排序', field: 'seq', visible: true, align: 'center', valign: 'middle'},
            {title: '二维码开关', field: 'qrShow', visible: true, align: 'center', valign: 'middle',formatter: function (value, row, index) {
                   if (value == 0) {
                        return '关';
                   } else if(value == 1){
                        return '开';
                       }
               }},
            {title: '二维码内容', field: 'qrInfo', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
special.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        special.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加
 */
special.openAddSpecial = function () {
    var index = layer.open({
        type: 2,
        title: '添加',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/special/special_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看详情
 */
special.openSpecialDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/special/special_update/' + special.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除
 */
special.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/special/delete", function (data) {
            Feng.success("删除成功!");
            special.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("specialId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询列表
 */
special.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    special.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = special.initColumn();
    var table = new BSTable(special.id, "/special/list", defaultColunms);
    table.setPaginationType("client");
    special.table = table.init();
});
