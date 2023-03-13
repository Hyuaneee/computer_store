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
        //购物车列表数据
        cartList:{
            content:[],
        },
        //收获地址列表
        addressList: [],
        //商品总件和商品总金额
        CartCount:{
            allProduct: 0,
            allmoney: 0
        },
        multipleSelection: [],
    },
    //钩子函数，VUE对象初始化完成后自动执行
    created(){
        this.getUser();
        this.findPage();
        this.getAddressList();
    },
    methods:{
        //获取用户信息
        getUser(){
            axios({
                methods: "get",
                url: "/user/getUser",
            }).then((res)=>{
                if(res.data.flag){
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
        //获取当前页购物车数据
        findPage(){
            //获取地址栏参数
            var params=location.search;
            params=params.substring(6);
            var arr=params.split(",");
            for(sb of arr){
                var num=parseInt(sb);
                this.multipleSelection.push(num);
            }

            axios({
                method: "POST",
                url: "/cart/getListCids",
                data:this.multipleSelection
            }).then((res)=>{
                if(res.data.flag){
                    this.cartList.content=res.data.data;
                    //计算总商品数和总金额
                    this.CartCount.allProduct=this.multipleSelection.length;
                    for(var index=0;index<this.cartList.content.length;index++){
                        var price=this.cartList.content[index].price;
                        var num=this.cartList.content[index].num;
                        this.CartCount.allmoney+=price*num;
                    }
                }else{
                    this.$message({
                        type:"into",
                        message:res.data.message
                    });
                }

            });
        },
        //获取当前页购物车数据
        getAddressList(){
            axios({
                method: "GET",
                url: "/address/getAddressList",
            }).then((res)=>{
                if(res.data.flag){
                    this.addressList=res.data.data;

                }else{
                    this.$message({
                        type:"into",
                        message:res.data.message
                    });
                }

            });
        },
        //创建订单
        saveOrder(){
            var aid=$('#address-list').val();
            if(aid.indexOf('请选择')>-1){
                this.$message.error('请选择地址');
            }else{
                this.$confirm("此操作将创建订单，是否继续？","提示",{
                    confirmButtonText:"确定",
                    cancelButtonText:"取消",
                    type: "warning"
                }).then(()=> {   //选择确定的情况
                    axios({
                        method: "POST",
                        url: "/order/insert/"+parseInt(aid),
                        data: this.multipleSelection,
                    }).then((res)=>{
                        if(res.data.flag){
                            this.$message({
                                type:"success",
                                message:res.data.message
                            });
                            setTimeout(function () {
                                location.href="payment.html?oid="+res.data.data;
                            },1500);
                        }else{
                            this.$message({
                                type:"into",
                                message:res.data.message
                            });
                        }

                    });
                }).catch(()=>{   //选择取消的情况
                    this.$message({
                        type:"into",
                        message:"已取消"
                    });
                });
            }
        }
    }
});