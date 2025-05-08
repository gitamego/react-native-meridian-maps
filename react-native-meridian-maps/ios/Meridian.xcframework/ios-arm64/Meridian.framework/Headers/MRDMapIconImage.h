//
//  MRDMapIconImage.h
//  Meridian
//
//  Created by Stephen Kelly on 26/2/2022.
//  Copyright © 2022 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
/// Enum to create Meridian facility images in ``MRDMapIconImage``
typedef NSString *MRDMapIconType NS_TYPED_ENUM;
extern MRDMapIconType const MRDMapIconTypeAED;
extern MRDMapIconType const MRDMapIconTypeAmusementPark;
extern MRDMapIconType const MRDMapIconTypeAssetBeacon;
extern MRDMapIconType const MRDMapIconTypeATM;
extern MRDMapIconType const MRDMapIconTypeAttraction;
extern MRDMapIconType const MRDMapIconTypeBaggage;
extern MRDMapIconType const MRDMapIconTypeBaggageCarts;
extern MRDMapIconType const MRDMapIconTypeBank;
extern MRDMapIconType const MRDMapIconTypeBar;
extern MRDMapIconType const MRDMapIconTypeBus;
extern MRDMapIconType const MRDMapIconTypeCafe;
extern MRDMapIconType const MRDMapIconTypeCafeteria;
extern MRDMapIconType const MRDMapIconTypeCashier;
extern MRDMapIconType const MRDMapIconTypeChangingRoom;
extern MRDMapIconType const MRDMapIconTypeChangingStation;
extern MRDMapIconType const MRDMapIconTypeChargingStation;
extern MRDMapIconType const MRDMapIconTypeClinic;
extern MRDMapIconType const MRDMapIconTypeClub;
extern MRDMapIconType const MRDMapIconTypeCoatCheck;
extern MRDMapIconType const MRDMapIconTypeConferenceRoom;
extern MRDMapIconType const MRDMapIconTypeCurrencyExchange;
extern MRDMapIconType const MRDMapIconTypeCustomerService;
extern MRDMapIconType const MRDMapIconTypeDesk;
extern MRDMapIconType const MRDMapIconTypeDeviceCharging;
extern MRDMapIconType const MRDMapIconTypeElevator;
extern MRDMapIconType const MRDMapIconTypeEmergencyDept;
extern MRDMapIconType const MRDMapIconTypeEscalator;
extern MRDMapIconType const MRDMapIconTypeExhibit;
extern MRDMapIconType const MRDMapIconTypeExit;
extern MRDMapIconType const MRDMapIconTypeFirstAid;
extern MRDMapIconType const MRDMapIconTypeFitnessCenter;
extern MRDMapIconType const MRDMapIconTypeFountain;
extern MRDMapIconType const MRDMapIconTypeGallery;
extern MRDMapIconType const MRDMapIconTypeGaming;
extern MRDMapIconType const MRDMapIconTypeGarden;
extern MRDMapIconType const MRDMapIconTypeGate;
extern MRDMapIconType const MRDMapIconTypeGeneric;
extern MRDMapIconType const MRDMapIconTypeGlobalEntryOffice;
extern MRDMapIconType const MRDMapIconTypeHandicap;
extern MRDMapIconType const MRDMapIconTypeInformation;
extern MRDMapIconType const MRDMapIconTypeKiosk;
extern MRDMapIconType const MRDMapIconTypeLab;
extern MRDMapIconType const MRDMapIconTypeLaptopLounge;
extern MRDMapIconType const MRDMapIconTypeLocationBeacon;
extern MRDMapIconType const MRDMapIconTypeLostFound;
extern MRDMapIconType const MRDMapIconTypeLounge;
extern MRDMapIconType const MRDMapIconTypeMailbox;
extern MRDMapIconType const MRDMapIconTypeMuseum;
extern MRDMapIconType const MRDMapIconTypeNursingStation;
extern MRDMapIconType const MRDMapIconTypeOperatingRoom;
extern MRDMapIconType const MRDMapIconTypePagingPhone;
extern MRDMapIconType const MRDMapIconTypeParking;
extern MRDMapIconType const MRDMapIconTypePetRelief;
extern MRDMapIconType const MRDMapIconTypePharmacy;
extern MRDMapIconType const MRDMapIconTypePhone;
extern MRDMapIconType const MRDMapIconTypePlayArea;
extern MRDMapIconType const MRDMapIconTypePrinter;
extern MRDMapIconType const MRDMapIconTypeProximityBeacon;
extern MRDMapIconType const MRDMapIconTypeRegistration;
extern MRDMapIconType const MRDMapIconTypeRentalCars;
extern MRDMapIconType const MRDMapIconTypeRestaurant;
extern MRDMapIconType const MRDMapIconTypeRestroom;
extern MRDMapIconType const MRDMapIconTypeRestroomADA;
extern MRDMapIconType const MRDMapIconTypeRestroomFamily;
extern MRDMapIconType const MRDMapIconTypeRestroomMen;
extern MRDMapIconType const MRDMapIconTypeRestroomMenADA;
extern MRDMapIconType const MRDMapIconTypeRestroomWomen;
extern MRDMapIconType const MRDMapIconTypeRestroomWomenADA;
extern MRDMapIconType const MRDMapIconTypeRewards;
extern MRDMapIconType const MRDMapIconTypeSecurity;
extern MRDMapIconType const MRDMapIconTypeSecurityCheckpoint;
extern MRDMapIconType const MRDMapIconTypeShoeShine;
extern MRDMapIconType const MRDMapIconTypeShop;
extern MRDMapIconType const MRDMapIconTypeSpa;
extern MRDMapIconType const MRDMapIconTypeStadium;
extern MRDMapIconType const MRDMapIconTypeStairs;
extern MRDMapIconType const MRDMapIconTypeSwimmingPool;
extern MRDMapIconType const MRDMapIconTypeTag;
extern MRDMapIconType const MRDMapIconTypeTaxi;
extern MRDMapIconType const MRDMapIconTypeTheater;
extern MRDMapIconType const MRDMapIconTypeTicketing;
extern MRDMapIconType const MRDMapIconTypeTours;
extern MRDMapIconType const MRDMapIconTypeTrain;
extern MRDMapIconType const MRDMapIconTypeTraining;
extern MRDMapIconType const MRDMapIconTypeValet;
extern MRDMapIconType const MRDMapIconTypeVendingMachines;
extern MRDMapIconType const MRDMapIconTypeWaterFountain;
extern MRDMapIconType const MRDMapIconTypeWedding;
extern MRDMapIconType const MRDMapIconTypeBike;
extern MRDMapIconType const MRDMapIconTypeFireExtinguisher;

/// Class used for creating UIImage versions of Meridian's internal icons
@interface MRDMapIconImage: NSObject

/// Returns a circular image of the given type and size with white tint and the default Meridian background color for the icon type
/// @param type Meridian map icon type
/// @param size Diameter of the icon
+ (nullable UIImage *)imageForIcon:(MRDMapIconType)type size:(CGFloat)size;

/// Returns a circular image of the given type, size, and tint color with the default Meridian background color for the icon type
/// @param type Meridian map icon type
/// @param size Diameter of the icon
/// @param color Tint color of the icon
+ (nullable UIImage *)imageForIcon:(MRDMapIconType)type size:(CGFloat)size color:(UIColor *)color;

/// Returns a circular image of the given type, size, tint and background color
/// @param type Meridian map icon type
/// @param size Diameter of the icon
/// @param color Tint color of the icon
/// @param background Background color of the icon
+ (nullable UIImage *)imageForIcon:(MRDMapIconType)type size:(CGFloat)size color:(UIColor *)color background:(nullable UIColor *)background;
@end

NS_ASSUME_NONNULL_END
