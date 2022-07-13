import 'package:flutter/material.dart';
import 'package:flutter_auth_buttons/flutter_auth_buttons.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:lottie/lottie.dart';
import 'package:penchant/controllers/channelcontroller.dart';
import 'package:penchant/model/user.dart';

class LoginPage extends StatelessWidget {
  const LoginPage({Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        backgroundColor: Colors.black,
        body: Container(
          child: Column(
            children: [
              buildLogo(),
              buildTitle(),
              buildIntroText(),
              buildLoginButtons(),
            ],
          ),
        ),
      ),
    );
  }

  Widget buildTitle() {
    return Padding(
      padding: const EdgeInsets.only(),
      child: Text('Two Cents',
          style: GoogleFonts.quicksand(
            textStyle: TextStyle(
              fontSize: 60,
              color: Colors.white,
              //shadows: shineShadow?.shadows,
            ),
          )),
    );
  }

  Widget buildIntroText() {
    var style = GoogleFonts.quicksand(
        textStyle: TextStyle(
      fontSize: 26,
      color: Colors.white,
      //shadows: shineShadow?.shadows,
    ));
    var padding = const EdgeInsets.all(8.0);

    return Column(
      children: [
        Padding(
          padding: padding,
          child: Text(
            'Be Kind.',
            style: style,
            textAlign: TextAlign.center,
          ),
        ),
        Padding(
          padding: padding,
          child: Text(
            'Be Heard.',
            style: style,
            textAlign: TextAlign.center,
          ),
        ),
        Padding(
          padding: padding,
          child: Text(
            'Be Discovered.',
            style: style,
            textAlign: TextAlign.center,
          ),
        ),
      ],
    );
  }

  Widget buildLoginButtons() {
    return Expanded(
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          GoogleSignInButton(
            onPressed: () async {
              var profile = await _handleSignIn();
              print(profile);
            },
            darkMode: true, // default: false
          ),
        ],
      ),
    );
  }

  Widget buildLogo() {
    return Stack(children: [
      Opacity(
          opacity: .7,
          child: Image(image: AssetImage('assets/media/screamingboy.jpg'))),
      Center(
        child: Lottie.asset('assets/animations/microphone.json',
            animate: true, height: 300, width: 300),
      )
    ]);
  }

  Future<FirebaseUser> _handleSignIn() async {
    final GoogleSignIn _googleSignIn = GoogleSignIn();
    final FirebaseAuth _auth = FirebaseAuth.instance;
    final GoogleSignInAccount googleUser = await _googleSignIn.signIn();
    final GoogleSignInAuthentication googleAuth =
        await googleUser.authentication;

    final AuthCredential credential = GoogleAuthProvider.getCredential(
      accessToken: googleAuth.accessToken,
      idToken: googleAuth.idToken,
    );

    final FirebaseUser user =
        (await _auth.signInWithCredential(credential)).user;
    print("signed in " + user.displayName);

    User foundUser = await User.fromGoogleId(user.uid);
    if (foundUser != null) {
      ChannelController.to.setCurrentUser(foundUser.id);
    } else {
      // TODO: Need to create a new user to map to into the DB.
      print("Couldn't map user.");
    }

    return user;
  }
}
