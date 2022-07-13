import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:get/get.dart';
import 'package:penchant/model/user.dart';
import 'package:penchant/services/dataservice.dart';

class Media {
  String id;
  String userId;
  String media;
  String text;
  Rx<User> _user = Rx<User>();

  Media({
    this.id,
    this.userId,
    this.media,
    this.text,
  });

  static List<Media> fromDocumentsSnapshot(QuerySnapshot snapshot) {
    List<Media> medias = List<Media>();

    if (snapshot != null) {
      snapshot.documents.forEach((element) async {
        Media media = Media.fromDocumentSnapshot(element);
        DataService.to.streamUser(media.userId).listen((event) {
          media._user.value = User.fromDocumentSnapshot(event);
          medias.add(media);
        });
      });
    }

    return medias;
  }

  factory Media.fromDocumentSnapshot(DocumentSnapshot snap) {
    return Media(
      id: snap.documentID,
      userId: snap["user"],
      media: snap["media"],
      text: snap["text"],
    );
  }

  User get user {
    return _user.value;
  }
}
