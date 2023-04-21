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
        //商品信息
        productData: {},
        //购物车信息
        insertLoading: false,  //是否已加入购物车
        cartData: {
            pid: null,
            price: 0,
            num: 1,
        },
        //收藏数据
        collectLoading: true,  //是否加入收藏
        collectId: null

    },
    //钩子函数，VUE对象初始化完成后自动执行
    created() {
        this.getUser();
        this.details();
        this.getCart();
        this.getCollect();
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
        //获取商品信息
        details() {
            var id = getUrlParam("id");  //获取地址栏id
            axios({
                methods: "get",
                url: "/product/details",
                params: {
                    id: id
                }
            }).then((res) => {
                if (res.data.code === 1) {
                    this.productData = res.data.data;
                } else {
                    this.$message.error(res.data.message);
                }
            });
        },
        //添加购物车信息
        insert() {
            this.cartData.pid = this.productData.id;
            this.cartData.price = this.productData.price,
                axios({
                    methods: "GET",
                    url: "/cart/insert",
                    params: this.cartData
                }).then((res) => {
                    if (res.data.code === 1) {
                        this.$message({
                            message: "添加成功，请到购物车中查看！",
                            type: "success"
                        });
                        this.getCart();
                    } else {
                        this.$message.error(res.data.message);
                    }
                });
        },
        buyNow() {
            location.href = "pay/orderConfirm.html?pid=" + this.productData.id + "&num=" + this.cartData.num;
        },
        //根据商品id获取购车信息
        getCart() {
            var pid = getUrlParam("id");  //获取地址栏id
            axios({
                methods: "GET",
                url: "/cart/getCart",
                params: {
                    pid: pid
                }
            }).then((res) => {
                if (res.data.code === 1) {
                    this.insertLoading = true;
                } else {
                    this.insertLoading = false;
                }
            });
        },
        //删除购物车信息
        deleteCart() {
            axios({
                methods: "GET",
                url: "/cart/delete/" + this.productData.id,
            }).then((res) => {
                if (res.data.code === 1) {
                    this.$message({
                        message: "已成功从购物车中移除！",
                        type: "success"
                    });
                    this.getCart();
                } else {
                    this.$message.error(res.data.message);
                }
            });
        },
        //加入收藏
        insertCollect() {
            axios({
                methods: "GET",
                url: "/collect/insertCollect/" + this.productData.id,
            }).then((res) => {
                if (res.data.code === 1) {
                    this.$message({
                        message: "已将商品添加到收藏中！",
                        type: "success"
                    });
                    this.collectLoading = true;
                } else {
                    this.$message.error(res.data.message);
                }
            }).finally(() => {
                this.getCollect();
            })
        },
        //查询是否已加入收藏
        getCollect() {
            var pid = getUrlParam("id");  //获取地址栏id
            axios({
                methods: "get",
                url: "/collect/getCollect/" + pid,
            }).then((res) => {
                if (res.data.code === 1) {
                    this.collectLoading = true;
                    this.collectId = res.data.data.id;
                    console.log(this.collectId);
                } else {
                    this.collectLoading = false;
                }
            });
        },
        //删除收藏
        deleteCollect() {
            axios({
                methods: "GET",
                url: "/collect/delete/" + this.collectId,
            }).then((res) => {
                if (res.data.code === 1) {
                    this.$message({
                        message: "已从收藏中移除商品！",
                        type: "success"
                    });
                    this.collectLoading = false;
                } else {
                    this.$message.error(res.data.message);
                }
            });
        },
    }
});