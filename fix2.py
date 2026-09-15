def fix_appcomp(filepath):
    with open(filepath, 'r') as f:
        c = f.read()
    c = c.replace('Dimens.SpacingNormal', 'Dimens.spaceStandard')
    c = c.replace('Dimens.AvatarSizeNormal', 'Dimens.avatarMedium')
    with open(filepath, 'w') as f:
        f.write(c)

fix_appcomp('app/src/main/java/com/example/ui/components/AppComponents.kt')

def fix_history(filepath):
    with open(filepath, 'r') as f:
        c = f.read()
    # Handle imports:
    # import com.example.ui.theme.AlertRed -> import com.example.presentation.theme.KisanEarthRed
    c = c.replace('import com.example.ui.theme.AlertRed', 'import com.example.presentation.theme.KisanEarthRed')
    c = c.replace('import com.example.ui.theme.AlertYellow', 'import com.example.presentation.theme.KisanHarvestGold')
    c = c.replace('import com.example.ui.theme.HealthyGreen', 'import com.example.presentation.theme.KisanEmerald')
    
    # Handle usage:
    c = c.replace('AlertRed', 'KisanEarthRed')
    c = c.replace('AlertYellow', 'KisanHarvestGold')
    c = c.replace('HealthyGreen', 'KisanEmerald')

    # Fix .copy(...) error: unresolved reference copy. This might be because the Color class wasn't imported correctly? 
    # Or maybe it's `.copy(alpha = ...)` on something that isn't a Color? Let's leave it and see if fixing the color fixes copy.
    
    with open(filepath, 'w') as f:
        f.write(c)

fix_history('app/src/main/java/com/example/ui/screens/HistoryScreen.kt')
