Vue.config.productionTip = false  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->

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
        //用户信息
        formData: {
            username: '',
            password1: '',
            password2: '',
            phone: '',
        },
        //表单规则
        rules: {
            username: [{required: true, message: '用户名不能为空', trigger: 'blur'},
                {min: 2, max: 80, message: '用户名不能小于2位,，大于80位', trigger: 'blur'},
            ],  //blur失去焦点
            password1: [{required: true, message: '密码不能为空', trigger: 'blur'},
                {pattern: /^[ a-zA-Z0-9]{6,30}$/, message: '密码不能小于6位,不能大于30位,无特殊字符', trigger: 'blur'},
            ],
            password2: [{required: true, message: '密码不能为空', trigger: 'blur'},
                {validator: pas, trigger: 'blur'}],
            phone: [{required: true, message: '手机号不能为空', trigger: 'blur'},
                {
                    pattern: /^1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\d{8}$/,
                    message: '请输入正确的手机号',
                    trigger: 'blur'
                },
            ],  //blur失去焦点
        },
    },
    //钩子函数，VUE对象初始化完成后自动执行
    created() {

    },
    methods: {
        //注册注册
        register() {
            this.$refs.formData.validate((valid) => {
                if (valid) {
                    axios({
                        method: "POST",
                        url: "/user/enroll",
                        data: {
                            username: this.formData.username,
                            password: this.formData.password2,
                            phone: this.formData.phone,
                        }
                    }).then((res) => {
                        if (res.data.code === 1) {
                            this.$message.success({
                                message: "注册成功，即将跳转到登录界面！",
                                type: 'success'
                            });
                            setTimeout(function () {
                                location.href = "login.html";
                            }, 1000)
                        } else {
                            this.$message.error(res.data.message);
                        }
                    });
                } else {
                    this.$message.error('此提交不符合规则');
                    return false;
                }
            });
        },
    }
});