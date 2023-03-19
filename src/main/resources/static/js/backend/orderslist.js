Vue.config.productionTip = false;  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->

new Vue({
    el: '#app',
    data: {
        //订单信息
        orders: {
            oid: null,
            totalPrice: 0,
        },
        //订单列表
        ordersMap: {},
    },
    //钩子函数，VUE对象初始化完成后自动执行
    created() {
        this.getList();
    },
    methods: {
        //根据oid获取订单列表
        getList(status) {
            axios({
                methods: "get",
                url: "/order/getList",
                params: {
                    status: status
                }
            }).then((res) => {
                if (res.data.code === 1) {
                    this.ordersMap = res.data.data;
                } else {
                    this.$message.error(res.data.message);
                }
            });
        },
        //时间格式化
        formatting(value) {
            var now = new Date();
            var zeroFill = function (value) {
                if (value < 10) {
                    value = '0' + value
                }
                return value
            }

            var year = now.getFullYear();
            //年
            var month = zeroFill(now.getMonth() + 1);
            //月
            var day = zeroFill(now.getDate());
            //日
            var hh = zeroFill(now.getHours());
            //时
            var mm = zeroFill(now.getMinutes());
            //分
            var ss = zeroFill(now.getSeconds());

            return (`${year}-${month}-${day} ${hh}:${mm}:${ss}`);
        },

        //商家发货
        updateItemStatus(id) {
            this.$confirm("您确认要发货吗？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {   //选择确定的情况
                axios({
                    methods: "get",
                    url: "/order/updateItemStatus/" + id,
                }).then((res) => {
                    if (res.data.code === 1) {
                        this.$message.success({
                            message: "商品已发货",
                            type: 'success'
                        });
                    } else {
                        this.$message.error(res.data.message);
                    }
                }).finally(() => {
                    this.getList(null);
                });
            }).catch(() => {   //选择取消的情况
                this.$message({
                    type: "into",
                    message: "已取消"
                });
            });
        },
        //判断staus状态
        getStatus(status) {
            if (status == 2 || status == 3 || status == 4) {
                return false;
            } else {
                return true;
            }
        },
        //显示订单状态,0-未支付，1-已支付，2-已取消，3-已关闭，4-已完成
        showStatus(status) {
            if (status == 0) {
                return '未支付';
            } else if (status == 1) {
                return '已支付';
            } else if (status == 2) {
                return '已取消';
            } else if (status == 3) {
                return '已关闭';
            } else {
                return '已完成';
            }
        },
        //用户取消订单
        changeStatus(oid) {
            this.$confirm("您确认取消用户订单？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {   //选择确定的情况
                axios({
                    methods: "get",
                    url: "/order/changeStatus/" + oid,
                }).then((res) => {
                    if (res.data.code === 1) {
                        this.$message.success({
                            message: res.data.message,
                            type: 'success'
                        });
                    } else {
                        this.$message.error(res.data.message);
                    }
                }).finally(() => {
                    this.getList(null);
                });
            }).catch(() => {   //选择取消的情况
                this.$message({
                    type: "into",
                    message: "已取消"
                });
            });
        },
        //支付
        payment(oid) {
            location.href = "pay/payment.html?oid=" + oid;
        }
    }
});