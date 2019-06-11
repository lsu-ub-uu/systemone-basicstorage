module se.uu.ub.cora.systemone.basicstorage {
	requires se.uu.ub.cora.logger;
	requires transitive se.uu.ub.cora.basicstorage;

	exports se.uu.ub.cora.systemone.basicstorage;

	provides se.uu.ub.cora.storage.RecordStorageProvider
			with se.uu.ub.cora.systemone.basicstorage.RecordStorageInMemoryReadFromDiskProvider;
	provides se.uu.ub.cora.storage.MetadataStorageProvider
			with se.uu.ub.cora.systemone.basicstorage.RecordStorageInMemoryReadFromDiskProvider;

}