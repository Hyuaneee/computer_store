//获取地址栏中的参数id
function getUrlParam(name) {
    //构造一个含有目标参数的正则表达式对象
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    //匹配目标参数
    var r = window.location.search.substr(1).match(reg);
    //返回参数
    if (r != null) {
        return unescape(r[2]);
    } else {
        return null;
    }
}

Vue.config.productionTip = false;  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->

new Vue({
    el: '#app',
    data: {
        userLoading: false,  //用户是否显示
        user: {
            uid: null,
            username: '',
            avatar: ''
        },
        //搜索
        searchData: '',
        //订单列表
        ordersMap: {
            keys: '',
            values: ''
        },
        //支付宝沙箱支付
        aliPay: {
            traceNo: '',
            totalAmount: 0.0
        },
    },
    //钩子函数，VUE对象初始化完成后自动执行
    created() {
        this.getUser();
        this.getOrderOid();
    },
    methods: {
        //获取用户信息
        getUser() {
            axios({
                methods: "get",
                url: "/user/getUser",
            }).then((res) => {
                if (res.data.code === 1) {
                    this.userLoading = true;
                    this.user = res.data.data;
                } else {
                    this.userLoading = false;
                }
            });
        },

        //搜索
        searchContent() {
            location.href = "../search.html?context=" + this.searchData;
        },

        //根据oid获取订单
        getOrderOid() {
            var oid = getUrlParam("oid");
            axios({
                methods: "get",
                url: "/order/getOrderOid/" + oid,
            }).then((res) => {
                if (res.data.code === 1) {
                    this.ordersMap.keys = res.data.data[0].key;
                    this.ordersMap.values = res.data.data[0].value;
                } else {
                    this.$message.error(res.data.message);
                }
            });
        },

        //支付金额
        payment(totalPrice) {
            var oid = getUrlParam("oid");
            this.$confirm("是否支付金额", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {
                this.aliPay.traceNo = oid;
                this.aliPay.totalAmount = totalPrice;

                //对接支付宝沙箱支付
                location.href = this.payURL();

                //直接修改订单状态
               /*axios({
                    methods: "get",
                    url: "/order/updateStatus/" + oid,
                }).then((res) => {
                    if (res.data.code === 1) {
                        this.$message.success({
                            message: "支付成功",
                            type: 'success'
                        });
                        setTimeout(function () {
                            location.href = "paySuccess.html?oid=" + oid;
                        }, 500);
                    } else {
                        this.$message.error(res.data.message);
                    }
                });*/

            }).catch(() => {   //选择取消的情况
                this.$message({
                    type: "into",
                    message: "已取消"
                });
            });
        },

        //支付跳转路径
        payURL: function () {
            return 'http://127.0.0.1/alipay/pay?oid=' + this.aliPay.traceNo + '&totalPrice=' + this.aliPay.totalAmount
        },

        //时间格式化
        formatting(value) {
            var now = new Date(value);
            //年
            var year = now.getFullYear() + '-';
            //月
            var month = (now.getMonth() + 1 < 10 ? '0' + (now.getMonth() + 1) : now.getMonth() + 1) + '-';
            //日
            var day = now.getDate() + ' ';
            if (now.getDate().toString().length === 1) {
                day = '0' + now.getDate() + ' '
            }
            //时
            var hh = now.getHours() + ':';
            if (now.getHours().toString().length === 1) {
                hh = '0' + now.getHours() + ':'
            }
            //分
            var mm = now.getMinutes() + ":";
            if (now.getMinutes().toString().length === 1) {
                mm = '0' + now.getMinutes() + ':'
            }
            //秒
            var ss = now.getSeconds();
            if (now.getSeconds().toString().length === 1) {
                ss = '0' + now.getSeconds() + ' '
            }
            return (year + month + day + hh + mm + ss);
        },
    }
});