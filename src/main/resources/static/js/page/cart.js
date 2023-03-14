Vue.config.productionTip = false;  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->

new Vue({
    el: '#app',
    data: {
        userLoading: false,  //用户是否显示
        numLoading: false,   //计数器是否禁用
        user: {
            uid: null,
            username: '',
            avatar: ''
        },
        //搜索
        searchData: null,
        //购物车列表数据
        cartList: {
            content: [],
        },
        //全选
        allSelect: false,
        //复选框cid数组
        multipleSelection: [],
        //商品总件和商品总金额
        CartCount: {
            allProduct: 0,
            allmoney: 0
        }
    },
    //钩子函数，VUE对象初始化完成后自动执行
    created() {
        this.getUser();
        this.findPage();
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
        //获取当前页数据
        findPage() {
            axios({
                method: "POST",
                url: "/cart/getPageList",
                data: {},
            }).then((res) => {
                console.log(res.data.data)
                this.cartList.content = res.data.data;
                $('#allSelect').prop("checked", false);
            });
        },
        //行删除
        delCartItem(cid) {
            this.$confirm("此操作将从数据库中永久修改数据，是否继续？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {   //选择确定的情况
                axios({
                    method: "get",
                    url: "/cart/delCartItem/" + cid,
                }).then((res) => {
                    if (res.data.code === 1) {
                        this.$message.success({
                            message: res.data.data,
                            type: 'success'
                        });
                    } else {
                        this.$message.error(res.data.message);
                    }

                }).finally(() => {
                    this.findPage();
                });
            }).catch(() => {     //选择取消的情况
                this.$message({
                    type: "into",
                    message: "已取消修改"
                });
            });

        },
        //集合删除
        deleteList() {
            this.$confirm("此操作将从数据库中永久修改数据，是否继续？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {   //选择确定的情况
                axios({
                    method: "POST",
                    url: "/cart/deleteList",
                    data: this.multipleSelection
                }).then((res) => {
                    if (res.data.code === 1) {
                        this.$message.success({
                            message: res.data.data,
                            type: 'success'
                        });
                    } else {
                        this.$message.error(res.data.data);
                    }
                }).finally(() => {
                    this.findPage();
                });
            }).catch(() => {   //选择取消的情况
                this.$message({
                    type: "into",
                    message: "已取消修改"
                });
            });
        },
        //计时器，增加购物车num
        handleChange(cid, data) {   //回调函数传递其他参数
            this.numLoading = true;
            axios({
                method: "PUT",
                url: "/cart/handleChange/" + cid + "/" + data[0],
            }).then((res) => {
                if (res.data.code === 1) {

                } else {
                    this.$message.error(res.data.message);
                }
            })
            this.numLoading = false;
        },
        //全选
        checkall(event) {
            var flag = event.target.checked;
            $('input[type="checkbox"]').prop('checked', flag);

            if (flag) {
                for (var index = 0; index < this.cartList.content.length; index++) {
                    this.multipleSelection.push(this.cartList.content[index].cid);
                    //计算总金额
                    var price = this.cartList.content[index].price;
                    var num = this.cartList.content[index].num;
                    this.CartCount.allmoney += price * num;
                }
            } else {
                this.multipleSelection.splice(index, this.multipleSelection.length);

                //计算总金额
                this.CartCount.allmoney = 0;
            }
            //计算商品总数
            this.CartCount.allProduct = this.multipleSelection.length;
        },
        //单选
        checkOne(event) {
            var flag = event.target.checked;
            var cid = event.target.value;
            if (flag) {
                this.multipleSelection.push(cid);
                //计算总金额
                for (var index = 0; index < this.cartList.content.length; index++) {
                    if (cid == this.cartList.content[index].cid) {
                        var price = this.cartList.content[index].price;
                        var num = this.cartList.content[index].num;
                        this.CartCount.allmoney += price * num;
                    }
                }
            } else {
                for (var index = 0; index < this.multipleSelection.length; index++) {
                    if (cid == this.multipleSelection[index]) {
                        this.multipleSelection.splice(index, 1);
                    }
                }
                //计算总金额
                for (var index = 0; index < this.cartList.content.length; index++) {
                    if (cid == this.cartList.content[index].cid) {
                        var price = this.cartList.content[index].price;
                        var num = this.cartList.content[index].num;
                        this.CartCount.allmoney -= price * num;
                    }
                }
            }
            //计算商品总数
            this.CartCount.allProduct = this.multipleSelection.length;
        },
        //结算购物车
        submitCart() {
            if (this.multipleSelection.length == 0) {
                this.$message({
                    type: "into",
                    message: "请勾选购物车中的商品"
                });
            } else {
                this.$confirm("此操作将结算已勾选的购物车信息，是否继续？", "提示", {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "warning"
                }).then(() => {   //选择确定的情况
                    location.href = "pay/orderConfirm.html?cids=" + this.multipleSelection;
                }).catch(() => {   //选择取消的情况
                    this.$message({
                        type: "into",
                        message: "已取消结算"
                    });
                });
            }
        }
    }
});