import 'package:cloud_firestore/cloud_firestore.dart';

class Channel {
  String id;
  String name;
  String ownerID;
  String summary;

  Channel({
    this.id,
    this.name,
    this.ownerID,
    this.summary,
  });

  String get cover {
    return 'http://storage.googleapis.com/penchant-b3980.appspot.com/channels/covers/${name.toLowerCase()}.jpg';
  }

  static List<Channel> fromDocumentsSnapshot(QuerySnapshot snapshot) {
    List<Channel> channels = List<Channel>();

    if (snapshot != null) {
      snapshot.documents.forEach((element) {
        channels.add(Channel.fromDocumentSnapshot(element));
      });
    }

    return channels;
  }

  factory Channel.fromDocumentSnapshot(DocumentSnapshot snap) {
    return Channel(
      id: snap.documentID,
      name: snap["name"],
      ownerID: snap["ownerID"],
      summary: snap['summary'],
    );
  }
}
