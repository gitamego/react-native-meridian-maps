//
//  MRDDirectionsAssistantProtocol.h
//  Meridian
//
//  Created by Cody Garvin on 2/23/18.
//  Copyright © 2018 Aruba Networks. All rights reserved.
//

#ifndef MRDDirectionsAssistantProtocol_h
#define MRDDirectionsAssistantProtocol_h

/**
 Actions of the buttons being tapped for the delegate to utilize. This helps
 determine what actually has happened. Also used internally for toggling button
 types and states.
 */
typedef NS_ENUM(NSInteger, MRDDirectionsAssistantAction) {
    
    /// The action for when the overview button is tapped.
    MRDDirectionsAssistantOverviewAction = 0,
    /// The action for when the end route button is tapped.
    MRDDirectionsAssistantEndRouteAction,
    /// The the action for when the resume button is tapped.
    MRDDirectionsAssistantResumeAction,
};

/**
 This delegate is responsible for relaying directions assistant actions.
 */
@protocol MRDDirectionsAssistantDelegate <NSObject>

/**
 When a button is tapped on the sheet this method is called to relay the action.
 
 @param action The action that was performed when tapping one of the direction
 buttons.
 */
- (void)didRequestDirectionsAction:(MRDDirectionsAssistantAction)action;

@end

#endif /* MRDDirectionsAssistantProtocol_h */
