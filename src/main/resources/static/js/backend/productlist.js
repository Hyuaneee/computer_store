Vue.config.productionTip = false  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->
const v = new Vue({
    el: '#app',
    data: {
        loading: false,   //页面加载,false:关闭,true:加载中
        dialogFormVisible: false,   //是否弹出的添加窗口
        dialogUpdateVisible: false,   //是否弹出的修改窗口

        //分页数据
        page: {
            content: [],
            currentPage: 1,   //当前页
            pageSize: 4,  //当前页条数
            total: 1,  //数据总条数
            pages: 1,  //总页数
            firstPage: false,  //是否禁用前一页按钮
            lastPage: false,  //是否有禁用下一页按钮
        },

        //修该和添加规则
        rules: {    //表单规则
            itemType: [{required: true, message: '不能为空', trigger: 'blur'}],  //blur失去焦点
            title: [{required: true, message: '不能为空', trigger: 'blur'}],
            price: [
                {required: true, message: '价格不能为空', trigger: 'blur'},
                {pattern: /^[1-9]\d*$/, message: '请输入正确的价格区间（不包含小数）', trigger: 'blur'},
            ],
            num: [
                {required: true, message: '数量不能为空', trigger: 'blur'},
                {pattern: /^[1-9]\d*$/, message: '请输入正确的数量区间（不包含小数）', trigger: 'blur'},
            ]
        },
        //修改窗口信息
        elUpdate: {},
        //添加窗口信息
        elAdd: {}
    },
    //钩子函数，VUE对象初始化完成后自动执行
    created() {
        this.findPage();
    },
    methods: {
        //获取当前页数据
        findPage() {
            this.loading = true;
            axios({
                method: "POST",
                url: "/product/getList/" + this.page.currentPage + "/" + this.page.pageSize,
                data: {},
            }).then((res) => {
                this.page.content = res.data.data.records;
                this.page.currentPage = res.data.data.current;
                this.page.pageSize = res.data.data.size;
                this.page.total = res.data.data.total;
                this.page.pages = res.data.data.pages;
                this.page.firstPage = this.page.currentPage == 1 ? true : false;
                this.page.lastPage = this.page.currentPage == this.page.pages ? true : false;
                setTimeout(function () {
                }, 1000)
                this.loading = false;  //关闭加载
            });
        },

        //上架下架
        setStatus(row) {
            this.$confirm("此操作将从更改商品状态，是否继续？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {   //选择确定的情况
                axios({
                    method: "get",
                    url: "/product/setStatus/" + row.id + "/" + row.status,
                }).then((res) => {
                    if (res.data.code === 1) {
                        this.$message.success({
                            message: "商品状态修改成功",
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

        //逻辑删除
        setDeleted(row) {
            this.$confirm("此操作将从删除商品，是否继续？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {   //选择确定的情况
                axios({
                    method: "get",
                    url: "/product/setDeleted/" + row.id,
                }).then((res) => {
                    if (res.data.code === 1) {
                        this.$message.success({
                            message: "商品已删除！",
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

        //显示添加页面
        handleAdd(row) {
            this.dialogFormVisible = true;
        },

        //提交添加信息
        handleSave() {
            this.$refs.elAdd.validate((valid) => {
                if (valid) {
                    this.$confirm("此操作将从数据库中永久修改数据，是否继续？", "提示", {
                        confirmButtonText: "确定",
                        cancelButtonText: "取消",
                        type: "warning"
                    }).then(() => {   //选择确定的情况
                        axios({
                            method: "POST",
                            url: "/product/addRow",
                            data: this.elAdd
                        }).then((res) => {
                            if (res.data.code === 1) {
                                this.$message({
                                    message: "商品添加成功",
                                    type: "success"
                                });
                            } else {
                                this.$message(res.data.message);
                            }
                        }).finally(() => {
                            this.findPage();
                        });
                    }).catch(() => {     //选择取消的情况
                        this.$message({
                            type: "into",
                            message: "已取消添加"
                        });
                    }).finally(() => {
                        this.dialogFormVisible = false;
                    });
                } else {
                    this.$message.error('此提交不符合规则');
                    return false;
                }
            });

        },

        //显示修改页面
        handleUpdate(row) {
            axios({
                method: "get",
                url: "/product/selectId/" + row.id,
            }).then((res) => {
                if (res.data.code === 0) {
                    this.$message(res.data.data);
                } else {
                    this.elUpdate = res.data.data;
                    this.dialogUpdateVisible = true;
                    //因为要获取所有信息，列表页要获取到
                }
            });
        },

        //提交修改信息
        handleEdit() {
            this.$refs.elUpdate.validate((valid) => {
                if (valid) {
                    this.$confirm("此操作将从数据库中永久修改数据，是否继续？", "提示", {
                        confirmButtonText: "确定",
                        cancelButtonText: "取消",
                        type: "warning"
                    }).then(() => {   //选择确定的情况
                        axios({
                            method: "PUT",
                            url: "/product/updateRow",
                            data: this.elUpdate
                        }).then((res) => {
                            if (res.data.code === 1) {
                                this.$message({
                                    message: res.data.data,
                                    type: "success"
                                });
                            } else {
                                this.$message(res.data.data);
                            }
                        }).finally(() => {
                            this.findPage();
                        });
                    }).catch(() => {     //选择取消的情况
                        this.$message({
                            type: "into",
                            message: "已取消修改"
                        });
                    }).finally(() => {
                        this.dialogUpdateVisible = false;
                    });
                } else {
                    this.$message.error('此提交不符合规则');
                    return false;
                }
            });
        },

        //重置输入框
        reset() {
            this.$refs.elAdd.resetFields();
        },
        //上一页
        Previous() {
            this.page.currentPage = this.page.currentPage - 1;
            this.findPage();
        },
        //下一页
        Next() {
            this.page.currentPage = this.page.currentPage + 1;
            this.findPage();
        },
        //添加和修改的取消
        cancel() {
            this.dialogFormVisible = false;
            this.dialogUpdateVisible = false;
        },
    }
});