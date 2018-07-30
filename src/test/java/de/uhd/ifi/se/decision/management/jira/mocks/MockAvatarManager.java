package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.jira.avatar.*;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.icon.IconOwningObjectId;
import com.atlassian.jira.icon.IconType;
import com.atlassian.jira.io.MediaConsumer;
import com.atlassian.jira.mock.MockAvatar;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MockAvatarManager implements AvatarManager {
	@Override
	public Avatar getById(Long aLong) throws DataAccessException {
		return null;
	}

	@Override
	public Avatar getByIdTagged(Long aLong) throws DataAccessException {
		return null;
	}

	@Override
	public boolean delete(Long aLong) throws DataAccessException {
		return false;
	}

	@Override
	public boolean delete(Long aLong, boolean b) {
		return false;
	}

	@Override
	public void update(Avatar avatar) throws DataAccessException {

	}

	@Nonnull
	@Override
	public Avatar create(Avatar avatar) throws DataAccessException {
		return new MockAvatar((long) 1, "Test", "Test", IconType.ISSUE_TYPE_ICON_TYPE, "SysAdmin", true);
	}

	@Nonnull
	@Override
	public Avatar create(Avatar avatar, InputStream inputStream, Selection selection)
			throws DataAccessException, IOException {
		return null;
	}

	@Nonnull
	@Override
	public Avatar create(Avatar.Type type, @Nonnull String s, @Nonnull AvatarImageDataProvider avatarImageDataProvider)
			throws IOException {
		return null;
	}

	@Nonnull
	@Override
	public Avatar create(@Nonnull IconType iconType, @Nonnull IconOwningObjectId iconOwningObjectId,
			@Nonnull AvatarImageDataProvider avatarImageDataProvider) throws IOException {
		return null;
	}

	@Nonnull
	@Override
	public Avatar create(String s, String s1, IconType iconType, IconOwningObjectId iconOwningObjectId,
			InputStream inputStream, Selection selection) throws DataAccessException, IOException {
		return null;
	}

	@Nonnull
	@Override
	public Avatar create(String s, String s1, Project project, InputStream inputStream, Selection selection)
			throws DataAccessException, IOException {
		return null;
	}

	@Nonnull
	@Override
	public Avatar create(String s, String s1, ApplicationUser applicationUser, InputStream inputStream,
			Selection selection) throws DataAccessException, IOException {
		return null;
	}

	@Nonnull
	@Override
	public List<Avatar> getAllSystemAvatars(Avatar.Type type) throws DataAccessException {
		return null;
	}

	@Nonnull
	@Override
	public List<Avatar> getAllSystemAvatars(IconType iconType) throws DataAccessException {
		return null;
	}

	@Nonnull
	@Override
	public List<Avatar> getCustomAvatarsForOwner(Avatar.Type type, String s) throws DataAccessException {
		return null;
	}

	@Nonnull
	@Override
	public List<Avatar> getCustomAvatarsForOwner(IconType iconType, String s) throws DataAccessException {
		return null;
	}

	@Override
	public boolean isAvatarOwner(Avatar avatar, String s) {
		return false;
	}

	@Override
	public void readAvatarData(Avatar avatar, ImageSize imageSize, Consumer<InputStream> consumer) throws IOException {

	}

	@Override
	public void readAvatarData(@Nonnull Avatar avatar, @Nonnull Avatar.Size size,
			@Nonnull Consumer<InputStream> consumer) throws IOException {

	}

	@Override
	public void readAvatarData(@Nonnull Avatar avatar, @Nonnull Avatar.Size size,
			@Nonnull AvatarFormatPolicy avatarFormatPolicy, @Nonnull MediaConsumer mediaConsumer) throws IOException {

	}

	@Nonnull
	@Override
	public File getAvatarBaseDirectory() {
		return null;
	}

	@Override
	public Long getDefaultAvatarId(@Nonnull Avatar.Type type) {
		return null;
	}

	@Override
	public Long getDefaultAvatarId(@Nonnull IconType iconType) {
		return null;
	}

	@Override
	public Avatar getDefaultAvatar(@Nonnull IconType iconType) {
		return null;
	}

	@Override
	public boolean isValidIconType(@Nonnull IconType iconType) {
		return false;
	}

	@Override
	public Long getAnonymousAvatarId() {
		return null;
	}

	@Override
	public boolean hasPermissionToView(ApplicationUser applicationUser, Avatar.Type type, String s) {
		return false;
	}

	@Override
	public boolean hasPermissionToView(ApplicationUser applicationUser, ApplicationUser applicationUser1) {
		return false;
	}

	@Override
	public boolean hasPermissionToView(ApplicationUser applicationUser, Project project) {
		return false;
	}

	@Override
	public boolean hasPermissionToEdit(ApplicationUser applicationUser, Avatar.Type type, String s) {
		return false;
	}

	@Override
	public boolean hasPermissionToEdit(ApplicationUser applicationUser, ApplicationUser applicationUser1) {
		return false;
	}

	@Override
	public boolean hasPermissionToEdit(ApplicationUser applicationUser, Project project) {
		return false;
	}

	@Override
	public boolean userCanView(@Nullable ApplicationUser applicationUser, @Nonnull Avatar avatar) {
		return false;
	}

	@Override
	public boolean userCanDelete(@Nullable ApplicationUser applicationUser, @Nonnull Avatar avatar) {
		return false;
	}

	@Override
	public boolean userCanCreateFor(@Nullable ApplicationUser applicationUser, @Nonnull IconType iconType,
			@Nonnull IconOwningObjectId iconOwningObjectId) {
		return false;
	}
}
