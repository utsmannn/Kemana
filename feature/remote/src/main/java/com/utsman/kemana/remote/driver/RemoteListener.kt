/*
 * Copyright (c) 2019 Muhammad Utsman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.utsman.kemana.remote.driver

interface RemoteListener {
    fun insertDriver(driverItem: Driver, driver: (success: Boolean, driver: Driver?) -> Unit)
    fun getDriversActive(list: (List<Driver>?) -> Unit)
    fun getDriversActiveEmail(email: (List<String>?) -> Unit)
    fun getDriver(id: String, driver: (Driver?) -> Unit)
    fun getDriver(id: String) : Driver?
    fun editDriver(id: String, position: Position, driver: (Driver?) -> Unit)
    fun editDriverByEmail(email: String, position: Position, driver: (Driver?) -> Unit)
    fun deleteDriver(id: String, status: (Boolean?) -> Unit)
    fun deleteDriverByEmail(email: String, status: (Boolean?) -> Unit)

    fun getDriversRegisteredEmail(email: String?, driver: (Driver?) -> Unit)
    fun registerDriver(driverItem: Driver, driver: (success: Boolean, driver: Driver?) -> Unit)
    fun checkRegisteredDriver(email: String?, hasRegister: (Boolean?) -> Unit)
    fun getRegisteredDriverById(id: String?, driver: (Driver?) -> Unit)
    fun getAttrRegisteredDriver(id: String?, attr: (Attribute?) -> Unit)
    fun editDriverRegisteredByEmail(email: String?, position: Position?, driver: (Driver?) -> Unit)
}