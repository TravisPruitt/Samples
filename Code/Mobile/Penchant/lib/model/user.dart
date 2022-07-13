import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:penchant/services/dataservice.dart';

class User {
  String id;
  int availableTime;
  String displayName;
  String mediaUrl;

  User(String id, String displayName, int availableTime, String mediaUrl) {
    this.id = id;
    this.displayName = displayName;
    this.availableTime = availableTime;
    this.mediaUrl = mediaUrl;
  }

  static Future<User> fromId(String id) async {
    var snapshot = await DataService.to.getUser(id);
    User user = User.fromDocumentSnapshot(snapshot);
    return user;
  }

  static Future<User> fromGoogleId(String googleId) async {
    var query = await DataService.to.getUserByGoogleId(googleId);

    return User(
      query.documents[0].documentID,
      query.documents[0].data["name"],
      query.documents[0].data["availableTime"],
      query.documents[0].data["media"],
    );
  }

  factory User.fromDocumentSnapshot(DocumentSnapshot snap) {
    User user;

    if (snap != null && snap.data != null) {
      user = User(
        snap.documentID,
        snap["name"],
        snap["availableTime"],
        snap["media"],
      );
    }

    return user;
  }
}
