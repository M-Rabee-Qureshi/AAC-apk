package com.example.data.local

import com.example.data.model.BrandEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ItemEntity
import com.example.data.model.SpecDefinitionEntity

object DefaultData {
    val defaultCategories = listOf(
        CategoryEntity(name = "Radiators"),
        CategoryEntity(name = "Heaters"),
        CategoryEntity(name = "Condensers"),
        CategoryEntity(name = "Cooling Fans"),
        CategoryEntity(name = "Tractor & Generator Parts")
    )

    val defaultBrands = listOf(
        BrandEntity(name = "AutoCool"),
        BrandEntity(name = "KoyoCool"),
        BrandEntity(name = "Koolex"),
        BrandEntity(name = "IMPORTED"),
        BrandEntity(name = "Toyota Genuine"),
        BrandEntity(name = "Suzuki Genuine"),
        BrandEntity(name = "Honda Genuine")
    )

    val defaultSpecs = listOf(
        SpecDefinitionEntity(name = "Core MM", defaultValue = "26"),
        SpecDefinitionEntity(name = "Transmission", defaultValue = "MT"),
        SpecDefinitionEntity(name = "Model Year", defaultValue = "2000-2015"),
        SpecDefinitionEntity(name = "Engine CC", defaultValue = "1300cc")
    )

    val defaultItems = listOf(
        // Imported Radiators
        ItemEntity(name = "Radiator Toyota Corolla 2009-13 MT", category = "Radiators", brand = "IMPORTED", rate = 9130.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Corolla 2009-13 AT", category = "Radiators", brand = "IMPORTED", rate = 12320.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Suzuki Swift 1988-95 MT", category = "Radiators", brand = "IMPORTED", rate = 13420.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Swift 2004-11 MT", category = "Radiators", brand = "IMPORTED", rate = 14300.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Cultus EFI MT", category = "Radiators", brand = "IMPORTED", rate = 7150.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Hyundai Santro 1998 MT", category = "Radiators", brand = "IMPORTED", rate = 7700.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Wagon-R 2014 MT", category = "Radiators", brand = "IMPORTED", rate = 10450.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Alto 2002-08 1000cc MT", category = "Radiators", brand = "IMPORTED", rate = 7150.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki APV (Carry) 2009 MT", category = "Radiators", brand = "IMPORTED", rate = 12650.0, mm = 32, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki APV (Carry) 2009 AT", category = "Radiators", brand = "IMPORTED", rate = 14300.0, mm = 32, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Corolla 2.D 2000-07 MT", category = "Radiators", brand = "IMPORTED", rate = 14300.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Corolla 2.D 2000-07 AT", category = "Radiators", brand = "IMPORTED", rate = 15950.0, mm = 26, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Corolla 2003-08 MT", category = "Radiators", brand = "IMPORTED", rate = 9130.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Corolla 2003-08 AT", category = "Radiators", brand = "IMPORTED", rate = 12430.0, mm = 26, transmission = "AT"),
        ItemEntity(name = "Radiator Hyundai Shahzore 1998-10 2500cc", category = "Radiators", brand = "IMPORTED", rate = 19140.0, mm = 32, transmission = "MT"),
        ItemEntity(name = "Radiator Daihatsu Coure (Mira) 1990-98 AT", category = "Radiators", brand = "IMPORTED", rate = 9294.0, mm = 26, transmission = "AT"),

        // KoyoCool Radiators
        ItemEntity(name = "Radiator Toyota Corolla 2D 93", category = "Radiators", brand = "KoyoCool", rate = 10300.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Corolla 2D 86", category = "Radiators", brand = "KoyoCool", rate = 9800.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Corolla Xli 2009-13 M/T", category = "Radiators", brand = "KoyoCool", rate = 7900.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Pickup", category = "Radiators", brand = "KoyoCool", rate = 5600.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Cultus New", category = "Radiators", brand = "KoyoCool", rate = 6000.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Wagon-R 2018", category = "Radiators", brand = "KoyoCool", rate = 8500.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Vitz Auto 1998-04", category = "Radiators", brand = "KoyoCool", rate = 8500.0, mm = 26, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Vitz New M/T", category = "Radiators", brand = "KoyoCool", rate = 8000.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Corolla Xli 2009-13 A/T", category = "Radiators", brand = "KoyoCool", rate = 9500.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Honda City 2005 M/T", category = "Radiators", brand = "KoyoCool", rate = 8800.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Honda Civic 2001-06 A/T", category = "Radiators", brand = "KoyoCool", rate = 9500.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Suzuki Liana M/T", category = "Radiators", brand = "KoyoCool", rate = 9700.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Honda Civic 2001-06 M/T", category = "Radiators", brand = "KoyoCool", rate = 8800.0, mm = 17, transmission = "MT"),
        ItemEntity(name = "Radiator Honda City 1997-02 M/T", category = "Radiators", brand = "KoyoCool", rate = 9200.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Honda Civic Reborn 2007 M/T", category = "Radiators", brand = "KoyoCool", rate = 10500.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Jimny A/T", category = "Radiators", brand = "KoyoCool", rate = 9800.0, mm = 26, transmission = "AT"),
        ItemEntity(name = "Radiator Suzuki Cultus New 2017", category = "Radiators", brand = "KoyoCool", rate = 10300.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Swift (OLD) 2009 A/T", category = "Radiators", brand = "KoyoCool", rate = 10500.0, mm = 26, transmission = "AT"),
        ItemEntity(name = "Radiator Honda Civic 1996 M/T", category = "Radiators", brand = "KoyoCool", rate = 6000.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Hilux RN 30/50 Diesel", category = "Radiators", brand = "KoyoCool", rate = 15000.0, mm = 26, transmission = "MT"),

        // Koolex Radiators
        ItemEntity(name = "Radiator Honda City 2024 A/T", category = "Radiators", brand = "Koolex", rate = 17500.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Honda City 2024 M/T", category = "Radiators", brand = "Koolex", rate = 15500.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Cultus New 2019", category = "Radiators", brand = "Koolex", rate = 6500.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Alto 660cc 2019", category = "Radiators", brand = "Koolex", rate = 6300.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Khyber M/T", category = "Radiators", brand = "Koolex", rate = 6000.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Baleno", category = "Radiators", brand = "Koolex", rate = 9800.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Wagon-R", category = "Radiators", brand = "Koolex", rate = 6500.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Honda Civic 2017", category = "Radiators", brand = "Koolex", rate = 12500.0, mm = 16, transmission = "AT"),

        // AutoCool Radiators
        ItemEntity(name = "Radiator Suzuki Cultus New", category = "Radiators", brand = "AutoCool", rate = 5200.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Pickup", category = "Radiators", brand = "AutoCool", rate = 5200.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Nissan Sunny 2004 Manual", category = "Radiators", brand = "AutoCool", rate = 11000.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Nissan Sunny 85-90 Manual", category = "Radiators", brand = "AutoCool", rate = 12500.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Nissan Sunny 2004 Automatic", category = "Radiators", brand = "AutoCool", rate = 12500.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Mitsubishi 4M40", category = "Radiators", brand = "AutoCool", rate = 19500.0, mm = 32, transmission = "MT"),
        ItemEntity(name = "Radiator Nissan Sunny 1993 Manual", category = "Radiators", brand = "AutoCool", rate = 13000.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Liana Automatic", category = "Radiators", brand = "AutoCool", rate = 10500.0, mm = 26, transmission = "AT"),
        ItemEntity(name = "Radiator Suzuki Swift A/T", category = "Radiators", brand = "AutoCool", rate = 11100.0, mm = 26, transmission = "AT"),
        ItemEntity(name = "Radiator Kia Spectra 2002 A/T", category = "Radiators", brand = "AutoCool", rate = 14500.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Chevrolet Aveo 1600cc A/T", category = "Radiators", brand = "AutoCool", rate = 14000.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Suzuki APV Van 05 M/T", category = "Radiators", brand = "AutoCool", rate = 11400.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Mitsubishi Lancer 2004 M/T", category = "Radiators", brand = "AutoCool", rate = 10500.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Chevrolet Optra 2006 A/T", category = "Radiators", brand = "AutoCool", rate = 12500.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Daihatsu Sirion/Boon/Passo A/T", category = "Radiators", brand = "AutoCool", rate = 13000.0, mm = 26, transmission = "AT"),
        ItemEntity(name = "Radiator Daihatsu Duet L70/80 1998-04 A/T", category = "Radiators", brand = "AutoCool", rate = 10300.0, mm = 26, transmission = "AT"),
        ItemEntity(name = "Radiator Hyundai Santro 2004 M/T", category = "Radiators", brand = "AutoCool", rate = 5500.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Daihatsu Coure A/T", category = "Radiators", brand = "AutoCool", rate = 6500.0, mm = 26, transmission = "AT"),
        ItemEntity(name = "Radiator Daihatsu Coure M/T", category = "Radiators", brand = "AutoCool", rate = 4800.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Alto 660cc M/T", category = "Radiators", brand = "AutoCool", rate = 11100.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Alto 660cc 1990-98 M/T", category = "Radiators", brand = "AutoCool", rate = 9300.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Cultus Old M/T", category = "Radiators", brand = "AutoCool", rate = 5200.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Corolla 86 Diesel M/T", category = "Radiators", brand = "AutoCool", rate = 8300.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Corolla 86 Petrol M/T", category = "Radiators", brand = "AutoCool", rate = 7800.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Suzuki Changan/Kalash ST-310 M/T", category = "Radiators", brand = "AutoCool", rate = 10800.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Premio (NZE240) 2000-01 A/T", category = "Radiators", brand = "AutoCool", rate = 14500.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Corona CT190/210 1996-01 M/T", category = "Radiators", brand = "AutoCool", rate = 14800.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Honda Accord CL7 2003 A/T", category = "Radiators", brand = "AutoCool", rate = 14500.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Prado KZJ95 Diesel 96-02 A/T", category = "Radiators", brand = "AutoCool", rate = 20500.0, mm = 32, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Hilux Tiger KDN166 A/T", category = "Radiators", brand = "AutoCool", rate = 15600.0, mm = 32, transmission = "AT"),
        ItemEntity(name = "Radiator Honda Accord Automatic 97-2002 A/T", category = "Radiators", brand = "AutoCool", rate = 13000.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Hiace KDH200 2004-18", category = "Radiators", brand = "AutoCool", rate = 20500.0, mm = 36, transmission = "MT"),
        ItemEntity(name = "Radiator Honda Civic 2013 M/T", category = "Radiators", brand = "AutoCool", rate = 11500.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Prado 2004 LJ120 A/T", category = "Radiators", brand = "AutoCool", rate = 24300.0, mm = 48, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Corolla 2D 2001-2008 Diesel", category = "Radiators", brand = "AutoCool", rate = 9400.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Land Cruiser 42TD A/T", category = "Radiators", brand = "AutoCool", rate = 23700.0, mm = 32, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Vitz 05-08 A/T", category = "Radiators", brand = "AutoCool", rate = 8000.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Hilux Vigo 007 LAN15 M/T", category = "Radiators", brand = "AutoCool", rate = 15300.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Hilux Vigo KUN 25 4X4", category = "Radiators", brand = "AutoCool", rate = 15300.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Hilux Vigo KUN 26 4X4", category = "Radiators", brand = "AutoCool", rate = 16300.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota RAV 4 ZCA 20", category = "Radiators", brand = "AutoCool", rate = 18000.0, mm = 26, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Camry 06-11 ACV40 A/T", category = "Radiators", brand = "AutoCool", rate = 17600.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Prius Inverter NHW-30", category = "Radiators", brand = "AutoCool", rate = 10500.0, mm = 32, transmission = "AT"),
        ItemEntity(name = "Radiator Honda City 2009 M/T", category = "Radiators", brand = "AutoCool", rate = 8800.0, mm = 16, transmission = "MT"),
        ItemEntity(name = "Radiator Honda City 2000 A/T", category = "Radiators", brand = "AutoCool", rate = 6500.0, mm = 26, transmission = "AT"),
        ItemEntity(name = "Radiator Nissan X-Trail 2001-07 M/T", category = "Radiators", brand = "AutoCool", rate = 19000.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Nissan Safari/Patrol Y60", category = "Radiators", brand = "AutoCool", rate = 21500.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Toyota Prado RZJ120 Petrol A/T", category = "Radiators", brand = "AutoCool", rate = 19500.0, mm = 32, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Prado KZJ120 Diesel", category = "Radiators", brand = "AutoCool", rate = 19000.0, mm = 32, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Land Cruiser Cygnus A/T", category = "Radiators", brand = "AutoCool", rate = 29000.0, mm = 32, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Mark X 2002", category = "Radiators", brand = "AutoCool", rate = 13000.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Prius 2003-09", category = "Radiators", brand = "AutoCool", rate = 14500.0, mm = 16, transmission = "AT"),
        ItemEntity(name = "Radiator Toyota Land Cruiser 98 HDJ100 A/T", category = "Radiators", brand = "AutoCool", rate = 29000.0, mm = 32, transmission = "AT"),
        ItemEntity(name = "Radiator Suzuki Mehran", category = "Radiators", brand = "AutoCool", rate = 5200.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator Shahzore Diesel", category = "Radiators", brand = "AutoCool", rate = 15500.0, mm = 26, transmission = "MT"),
        ItemEntity(name = "Radiator MF-375 / 385 Tractor", category = "Tractor & Generator Parts", brand = "AutoCool", rate = 18500.0, mm = 36, transmission = "MT"),

        // Heaters
        ItemEntity(name = "Heater Core Santro", category = "Heaters", brand = "AutoCool", rate = 4500.0, mm = 16, transmission = "Universal"),
        ItemEntity(name = "Heater Core Shahzore", category = "Heaters", brand = "AutoCool", rate = 5200.0, mm = 26, transmission = "Universal"),
        ItemEntity(name = "Heater Core Alto 1000cc", category = "Heaters", brand = "AutoCool", rate = 3800.0, mm = 16, transmission = "Universal"),
        ItemEntity(name = "Heater Core Wagon-R", category = "Heaters", brand = "AutoCool", rate = 4200.0, mm = 16, transmission = "Universal"),
        ItemEntity(name = "Heater Core Mehran", category = "Heaters", brand = "AutoCool", rate = 3200.0, mm = 16, transmission = "Universal"),
        ItemEntity(name = "Heater Core Cultus 2017 Euro", category = "Heaters", brand = "AutoCool", rate = 4600.0, mm = 16, transmission = "Universal"),
        ItemEntity(name = "Heater Core Honda City 2005", category = "Heaters", brand = "AutoCool", rate = 4800.0, mm = 16, transmission = "Universal")
    )
}
