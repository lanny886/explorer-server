/**
 * 管理初始化
 */
var Banner = {
    id: "bannerTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Banner.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: '横幅ID', field: 'id', visible: true, align: 'center', valign: 'middle'},
            {title: '横幅图片', field: 'imageUrl', visible: true, align: 'center', valign: 'middle'},
            {title: '横幅链接', field: 'url', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
Banner.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        Banner.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加
 */
Banner.openAddBanner = function () {
    var index = layer.open({
        type: 2,
        title: '添加',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/banner/banner_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看详情
 */
Banner.openBannerDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/banner/banner_update/' + Banner.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除
 */
Banner.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/banner/delete", function (data) {
            Feng.success("删除成功!");
            Banner.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("bannerId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询列表
 */
Banner.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    Banner.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = Banner.initColumn();
    var table = new BSTable(Banner.id, "/banner/list", defaultColunms);
    table.setPaginationType("client");
    Banner.table = table.init();
});
