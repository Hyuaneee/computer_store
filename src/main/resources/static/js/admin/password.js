Vue.config.productionTip = false  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->
//两次密码的效验
const pas = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入密码'));
    } else if (value !== v.$data.formData.password1) {
        callback(new Error('两次输入密码不一致!'));
    } else {
        callback();
    }
};
const v = new Vue({
    el: '#app',
    data: {
        //修改按钮是否禁用
        loading: false,
        //倒计时
        showTime: false,   //修改按钮上的倒计时是否显示
        downTime: 0,  //倒计时
        searchData: '',
        //用户信息
        formData: {
            password: '',
            password1: '',
            password2: '',
        },
        //获取用户信息
        user: {
            uid: null,
            username: '',
            avatar: ''
        },
        //表单规则
        rules: {
            password: [{required: true, message: '密码不能为空', trigger: 'blur'},
                {pattern: /^[ a-zA-Z0-9]{6,30}$/, message: '密码不能小于6位,不能大于30位,无特殊字符', trigger: 'blur'},
            ],
            password1: [{required: true, message: '密码不能为空', trigger: 'blur'},
                {pattern: /^[ a-zA-Z0-9]{6,30}$/, message: '密码不能小于6位,不能大于30位,无特殊字符', trigger: 'blur'},
            ],
            password2: [{required: true, message: '密码不能为空', trigger: 'blur'},
                {required: true, validator: pas, trigger: 'blur'}
            ],
        },
    },
    //钩子函数，VUE对象初始化完成后自动执行
    created() {
        this.getUser();
    },
    methods: {
        //获取用户信息
        getUser() {
            axios({
                method: "get",
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
            location.href = "../../page/search.html?context=" + this.searchData;
        },
        //实现倒计时
        Time() {
            this.downTime = 30;
            var time = setInterval(() => {
                this.downTime -= 1;
                if (this.downTime == 0) {
                    this.showTime = false;
                    this.loading = false,
                        clearInterval(time);
                }
            }, 1000)
        },
        //密码更新
        update() {
            this.loading = true;
            this.showTime = true;
            this.$refs.formData.validate((valid) => {
                if (valid) {
                    axios({
                        method: "PUT",
                        url: "/user/update/" + this.formData.password + "/" + this.formData.password2,
                    }).then((res) => {
                        if (res.data.code === 1) {
                            this.$message.success({
                                message: "密码修改成功，即将返回登陆界面！",
                                type: 'success'
                            });
                            setTimeout(function () {
                            }, 1000);
                            location.href = "/admin/logout";
                        } else {
                            this.$message.error(res.data.message);
                        }
                    });
                } else {
                    this.$message.error('此提交不符合规则');
                    return false;
                }
            });
            this.Time();
            this.formData.password = '';
            this.formData.password1 = '';
            this.formData.password2 = '';
        },
    }
});