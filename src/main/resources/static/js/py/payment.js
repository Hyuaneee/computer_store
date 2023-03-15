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

Vue.config.productionTip=false;  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->

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
        searchData: null,
        //订单信息
        order:{
            oid: null,
            totalPrice: 0,
        },
    },
    //钩子函数，VUE对象初始化完成后自动执行
    created(){
        this.getUser();
        this.getOrderOid();
    },
    methods:{
        //获取用户信息
        getUser(){
            axios({
                methods: "get",
                url: "/user/getUser",
            }).then((res)=>{
                if(res.data.code === 1){
                    this.userLoading=true;
                    this.user=res.data.data;
                }else{
                    this.userLoading=false;
                }
            });
        },
        //搜索
        searchContent(){
            location.href="search.html?context="+this.searchData;
        },
        //根据oid获取订单
        getOrderOid(){
            var oid=getUrlParam("oid");
            axios({
                methods: "get",
                url: "/order/getOrderOid/"+oid,
            }).then((res)=>{
                if(res.data.code === 1){
                    this.order=res.data.data;
                }else{
                    this.$message.error(res.data.message);
                }
            });
        },
        //支付金额
        payment(){
            var oid=getUrlParam("oid");

            this.$confirm("是否支付金额","提示",{
                confirmButtonText:"确定",
                cancelButtonText:"取消",
                type: "warning"
            }).then(()=> {   //选择确定的情况
                var payMethod=0;    //支付方式：1支付宝，2微信，3网银，4货到付款
                var radio = $('input[name="optionsRadios"]');
                for(var i=0; i<radio.length; i ++){
                    // 判断该单选框是否处于选中状态
                    if(radio[i].checked){
                        payMethod=radio[i].value;
                    }
                }
                console.log(payMethod);
                axios({
                    methods: "get",
                    url: "/order/updateStatus/"+oid,
                }).then((res)=>{
                    if(res.data.code === 1){
                        this.$message.success({
                            message: res.data.message,
                            type: 'success'
                        });
                        setTimeout(function () {
                            location.href="paySuccess.html?oid="+oid;
                        },1500);
                    }else{
                        this.$message.error(res.data.message);
                    }
                });

            }).catch(()=>{   //选择取消的情况
                this.$message({
                    type:"into",
                    message:"已取消"
                });
            });
        },

    }
});