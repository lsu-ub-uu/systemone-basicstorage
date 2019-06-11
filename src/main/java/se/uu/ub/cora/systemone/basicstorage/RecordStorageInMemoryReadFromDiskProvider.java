/*
 * Copyright 2019 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.systemone.basicstorage;

import java.util.Map;

import se.uu.ub.cora.basicstorage.DataStorageException;
import se.uu.ub.cora.basicstorage.RecordStorageInMemoryReadFromDisk;
import se.uu.ub.cora.basicstorage.RecordStorageInstance;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.storage.MetadataStorageProvider;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.RecordStorageProvider;

public class RecordStorageInMemoryReadFromDiskProvider
		implements RecordStorageProvider, MetadataStorageProvider {
	private Logger log = LoggerProvider
			.getLoggerForClass(RecordStorageInMemoryReadFromDiskProvider.class);
	private Map<String, String> initInfo;

	@Override
	public int getOrderToSelectImplementionsBy() {
		return 1;
	}

	@Override
	public void startUsingInitInfo(Map<String, String> initInfo) {
		this.initInfo = initInfo;
		log.logInfoUsingMessage(
				"RecordStorageInMemoryReadFromDiskProvider starting RecordStorageInMemoryReadFromDisk...");
		startRecordStorage();
		log.logInfoUsingMessage(
				"RecordStorageInMemoryReadFromDiskProvider started RecordStorageInMemoryReadFromDisk");
	}

	private void startRecordStorage() {
		if (noRunningRecordStorageExists()) {
			startNewRecordStorageOnDiskInstance();
		} else {
			useExistingRecordStorage();
		}
	}

	private boolean noRunningRecordStorageExists() {
		return RecordStorageInstance.instance == null;
	}

	private void startNewRecordStorageOnDiskInstance() {
		String basePath = tryToGetInitParameter("storageOnDiskBasePath");
		setStaticInstance(
				RecordStorageInMemoryReadFromDisk.createRecordStorageOnDiskWithBasePath(basePath));
	}

	private void useExistingRecordStorage() {
		log.logInfoUsingMessage("Using previously started RecordStorage as RecordStorage");
	}

	static void setStaticInstance(RecordStorage recordStorage) {
		RecordStorageInstance.instance = recordStorage;
	}

	private String tryToGetInitParameter(String parameterName) {
		throwErrorIfKeyIsMissingFromInitInfo(parameterName);
		String parameter = initInfo.get(parameterName);
		log.logInfoUsingMessage("Found " + parameter + " as " + parameterName);
		return parameter;
	}

	private void throwErrorIfKeyIsMissingFromInitInfo(String key) {
		if (!initInfo.containsKey(key)) {
			String errorMessage = "InitInfo must contain " + key;
			log.logFatalUsingMessage(errorMessage);
			throw DataStorageException.withMessage(errorMessage);
		}
	}

	@Override
	public RecordStorage getRecordStorage() {
		return RecordStorageInstance.instance;
	}

	@Override
	public MetadataStorage getMetadataStorage() {
		return (MetadataStorage) RecordStorageInstance.instance;
	}

}
