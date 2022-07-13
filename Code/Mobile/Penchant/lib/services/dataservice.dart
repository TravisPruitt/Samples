import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:get/get.dart';

class DataService {
  static DataService to = Get.find();
  final Firestore _db = Firestore.instance;

  Stream<QuerySnapshot> streamChannels() {
    return _db.collection('categories').snapshots();
  }

  Stream<DocumentSnapshot> streamChannel(String channelID) {
    return _db.collection('categories').document(channelID).snapshots();
  }

  Stream<QuerySnapshot> streamChannelMedia(List<String> tags) {
    return _db
        .collection('messages')
        .where('tags', arrayContainsAny: tags)
        .snapshots();
  }

  Stream<DocumentSnapshot> streamUser(String userID) {
    return _db.collection('users').document(userID).snapshots();
  }

  void updateUserTime(String userId, int updatedTime) async {
    _db
        .collection('users')
        .document(userId)
        .updateData({'availableTime': updatedTime});
  }

  Future<QuerySnapshot> getUserByGoogleId(String googleId) {
    return _db
        .collection('users')
        .where('uid', isEqualTo: googleId)
        .snapshots()
        .first;
    //.snapshots();
  }

  Future<DocumentSnapshot> getUser(String id) {
    return _db.collection('users').document(id).snapshots().first;
  }

  Stream<QuerySnapshot> streamQueue(String defaultChannelId) {
    return DataService.to.streamChannelMedia([defaultChannelId]);
  }

  Future<String> addMedia() async {
    DocumentReference ref = await _db.collection("messages").add({});
    return (ref.documentID);
  }

  Future<void> updateMedia(String documentId, String mediaPath) async {
    await _db.collection("messages").document(documentId).updateData({
      'media': mediaPath,
      'created': Timestamp.fromMillisecondsSinceEpoch(
          DateTime.now().millisecondsSinceEpoch),
    });
  }

  Future<void> addMediaTag(String documentId, String tag) async {
    DocumentReference ref = _db.collection("messages").document(documentId);

    ref.get().then((snapshot) {
      var existingTags = snapshot['tags'];

      if (existingTags == null) {
        existingTags = List<String>();
      }

      existingTags.add(tag);

      _db
          .collection("messages")
          .document(documentId)
          .updateData({'tags': existingTags});
    });
  }
}
