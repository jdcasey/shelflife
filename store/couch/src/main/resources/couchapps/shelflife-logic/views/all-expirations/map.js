function(doc){
	if( doc.doctype == 'expiration' ){
		emit(doc.name,{'_id': doc._id});
	}
}
