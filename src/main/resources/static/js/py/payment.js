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
        //订单信息
        order: {
            oid: null,
            totalPrice: 0,
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
            location.href = "search.html?context=" + this.searchData;
        },
        //根据oid获取订单
        getOrderOid() {
            var oid = getUrlParam("oid");
            axios({
                methods: "get",
                url: "/order/getOrderOid/" + oid,
            }).then((res) => {
                if (res.data.code === 1) {
                    this.order = res.data.data;
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
                //location.href = this.payURL();

                axios({
                    methods: "get",
                    url: "/order/updateStatus/" + oid,
                }).then((res) => {
                    if (res.data.code === 1) {
                        this.$message.success({
                            message: "订单已支付",
                            type: 'success'
                        });
                        setTimeout(function () {
                            location.href = "paySuccess.html?oid=" + oid;
                        }, 1500);
                    } else {
                        this.$message.error(res.data.message);
                    }
                });

            }).catch(() => {   //选择取消的情况
                this.$message({
                    type: "into",
                    message: "已取消"
                });
            });
        },
        //支付跳转路径
        payURL: function () {
            return 'http://localhost/alipay/pay?subject=收购阿里&traceNo=' + this.aliPay.traceNo + '&totalAmount=' + this.aliPay.totalAmount
        }

    }
});