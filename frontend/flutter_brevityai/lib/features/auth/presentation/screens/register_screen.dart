import 'package:animate_do/animate_do.dart';
import 'package:flutter/material.dart';
import '../../provider/auth_provider.dart';
import 'package:provider/provider.dart';


class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}


class _RegisterScreenState extends State<RegisterScreen> {

  final TextEditingController usernameController = TextEditingController();
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();  
  final TextEditingController confirmController = TextEditingController();


  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context);

 return Scaffold(
  resizeToAvoidBottomInset: true,
  body: Container(
    width: double.infinity,
    decoration: BoxDecoration(
      gradient: LinearGradient(
        begin: Alignment.topCenter,
        colors: [
          Colors.red.shade600,
          Colors.red.shade500,
          Colors.red.shade400,
        ],
      ),
    ),
    
    child: Stack(
      children: [
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            SizedBox(height: 0),
            Padding(
              padding: EdgeInsets.all(10),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  SizedBox(height: 10),
                    Align(
                      alignment: Alignment.topCenter,
                      child: Padding(
                        padding: const EdgeInsets.only(top: 100),
                        child: FadeInUp(
                          duration: Duration(milliseconds: 1200),
                          child: Container(
                            decoration: BoxDecoration(),
                            padding: EdgeInsets.all(6),
                            child: Icon(
                              Icons.star_rounded,
                              color: Colors.yellow[700],
                              size: 75,
                            ),
                          ),
                        ),
                      ),
                    ),
                ],
              ),
            ),
            SizedBox(height: 20),
            Expanded(
              child: Container(
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.only(
                    topLeft: Radius.circular(60),
                    topRight: Radius.circular(60),
                  ),
                ),
                child: SingleChildScrollView(
                  padding: EdgeInsets.only(
                    left: 30,
                    right: 30,
                    top:30,
                    bottom: MediaQuery.of(context).viewInsets.bottom + 30,
                    ),
                  child: Column(
                    children: <Widget>[
                      SizedBox(height: 10),
                      FadeInUp(duration: Duration(milliseconds: 1000), 
                      child: Text("Welcome to Viet Lingua", 
                      style: TextStyle(
                        color: Colors.grey.shade500, 
                        fontSize: 30
                        ),
                      )
                    ),
                      SizedBox(height: 10,),
                      FadeInUp(duration: Duration(milliseconds: 1300), 
                      child: Text("Create Your Account", 
                      style: TextStyle(
                        color: Colors.grey.shade500, 
                        fontSize: 25
                        ),
                      )
                    ),
                      SizedBox(height: 30,),
                      FadeInUp(
                        duration: Duration(milliseconds: 1400),
                        child: Container(
                          decoration: BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(10),
                            boxShadow: [
                              BoxShadow(
                                color: Color.fromRGBO(225, 95, 27, .3),
                                blurRadius: 20,
                                offset: Offset(0, 10),
                              )
                            ],
                          ),
                          child: Column(
                            children: <Widget>[
                              Container(
                                padding: EdgeInsets.all(10),
                                decoration: BoxDecoration(
                                  border: Border(
                                    bottom: BorderSide(
                                      color: Colors.grey.shade200,
                                    ),
                                  ),
                                ),
                                child: TextField(
                                  controller: emailController,
                                  decoration: InputDecoration(
                                    hintText: "Email Address",
                                    hintStyle: TextStyle(color: Colors.grey),
                                    prefixIcon: Icon(Icons.email),
                                    border: InputBorder.none,
                                  ),
                                ),
                              ),
                              Container(
                                padding: EdgeInsets.all(10),
                                decoration: BoxDecoration(
                                  border: Border(
                                    bottom: BorderSide(
                                      color: Colors.grey.shade200,
                                    ),
                                  ),
                                ),
                                child: TextField(
                                  controller: usernameController,
                                  decoration: InputDecoration(
                                    hintText: "Username",
                                    hintStyle: TextStyle(color: Colors.grey),
                                    prefixIcon: Icon(Icons.person),
                                    border: InputBorder.none,
                                  ),
                                ),
                              ),
                            Container(
                                padding: EdgeInsets.all(10),
                                decoration: BoxDecoration(
                                  border: Border(
                                    bottom: BorderSide(
                                      color: Colors.grey.shade200,
                                    ),
                                  ),
                                ),
                                child: TextField(
                                  controller: passwordController,
                                  obscureText: true,
                                  decoration: InputDecoration(
                                    hintText: "Password",
                                    hintStyle: TextStyle(color: Colors.grey),
                                    prefixIcon: Icon(Icons.lock),
                                    border: InputBorder.none,
                                  ),
                                ),
                              ),
                            Container(
                                padding: EdgeInsets.all(10),
                                decoration: BoxDecoration(
                                  border: Border(
                                    bottom: BorderSide(
                                      color: Colors.grey.shade200,
                                    ),
                                  ),
                                ),
                                child: TextField(
                                  controller: confirmController,
                                  obscureText: true,
                                  decoration: InputDecoration(
                                    hintText: "Confirm Password",
                                    hintStyle: TextStyle(color: Colors.grey),
                                    prefixIconColor: Colors.red,
                                    prefixIcon: Icon(Icons.lock_outline),
                                    border: InputBorder.none,
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                      SizedBox(height: 40),
                      FadeInUp(
                        duration: Duration(milliseconds: 1500),
                        child: GestureDetector(
                            onTap: () {
                                Navigator.pushNamed(context, '/login');
                            },
                        child: Text(
                        "Already Have An Account? Tap here to log in",
                        style: TextStyle(color: Colors.grey),
                        ),
                      ),
                    ),
                      
                      SizedBox(height: 20),
                      FadeInUp(
                        duration: Duration(milliseconds: 1600),
                        child: MaterialButton(
                          onPressed: authProvider.isLoading
                              ? null
                              : () async {
                                 final status = await authProvider.register(
                                    usernameController.text,
                                    emailController.text,
                                    passwordController.text,
                                    confirmController.text,
                                  );
                                  if (status == "User created successfully") {
                                    
                                    //Navigator.pushReplacementNamed(context,'/login');

                                    
                                    ScaffoldMessenger.of(context).showSnackBar(
                                      const SnackBar(
                                        content: Text("Account successfully created"),
                                      ),
                                    );
                                  
                                    //await Future.delayed(Duration(seconds:2));
                                    Navigator.pushReplacementNamed(context,'/login');
                                  } else {
                                    ScaffoldMessenger.of(context).showSnackBar(
                                       SnackBar(
                                        content: Text(status),
                                      ),
                                    );
                                  }
                                },
                          height: 50,
                          color: Colors.red[400],
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(50),
                          ),
                          child: Center(
                            child: Text(
                              "Create Account",
                              style: TextStyle(
                                color: Colors.white,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                        ),
                      ),
                      SizedBox(height: 50),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),

      ],
    ),
  ),
);
}
}