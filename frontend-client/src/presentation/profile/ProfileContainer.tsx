import { useState, useEffect } from "react";
import { ProfileForm } from "./ProfileForm";
import { ProfileList } from "./ProfileList";
import { ProfileDetail } from "./ProfileDetail";
import { ProfilePresenter } from "./ProfilePresenter";
import { CreateProfileUseCase } from "../../application/profile/CreateProfileUseCase";
import { GetProfileListUseCase } from "../../application/profile/GetProfileListUseCase";
import { GetProfileDetailUseCase } from "../../application/profile/GetProfileDetailUseCase";
import { UpdateProfileUseCase } from "../../application/profile/UpdateProfileUseCase";
import { DeleteProfileUseCase } from "../../application/profile/DeleteProfileUseCase";
import { ProfileApiRepository } from "../../infrastructure/http/ProfileApiRepository";
import { ProfileDomainService } from "../../domain/profile/services/ProfileDomainService";
import type { ProfileResponse } from "../../domain/profile/dto/ProfileResponse";
import type { UpdateProfileRequest } from "../../domain/profile/dto/UpdateProfileRequest";

export const ProfileContainer = () => {
  // Use only REST API with Axios
  const [repository] = useState(() => new ProfileApiRepository());

  const createUseCase = new CreateProfileUseCase(
    repository,
    new ProfileDomainService()
  );

  const getListUseCase = new GetProfileListUseCase(repository);
  const getDetailUseCase = new GetProfileDetailUseCase(repository);
  const updateProfileUseCase = new UpdateProfileUseCase(repository);
  const deleteProfileUseCase = new DeleteProfileUseCase(repository);

  // Form state
  const [values, setValues] = useState(ProfilePresenter.initialState());
  const [formLoading, setFormLoading] = useState(false);
  const [formError, setFormError] = useState<string>();

  // List state
  const [profiles, setProfiles] = useState<ProfileResponse[]>([]);
  const [listLoading, setListLoading] = useState(false);
  const [listError, setListError] = useState<string | null>(null);

  // Detail modal state
  const [selectedProfile, setSelectedProfile] = useState<ProfileResponse | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState<string | null>(null);

  // Load profiles on mount
  useEffect(() => {
    loadProfiles();
  }, []);

  const loadProfiles = async () => {
    setListLoading(true);
    setListError(null);

    try {
      const profileList = await getListUseCase.execute();
      setProfiles(profileList);
    } catch (e: any) {
      setListError(e.message);
    } finally {
      setListLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    setFormLoading(true);
    setFormError(undefined);

    try {
      await createUseCase.execute(values);
      alert("Profile created successfully");
      setValues(ProfilePresenter.initialState());
      // Refresh the list after creating
      loadProfiles();
    } catch (e: any) {
      setFormError(e.message);
    } finally {
      setFormLoading(false);
    }
  };

  const handleProfileClick = async (userId: string) => {
    setDetailLoading(true);
    setDetailError(null);
    setSelectedProfile(null);

    try {
      const profile = await getDetailUseCase.execute(userId);
      setSelectedProfile(profile);
    } catch (e: any) {
      setDetailError(e.message);
    } finally {
      setDetailLoading(false);
    }
  };

  const handleCloseDetail = () => {
    setSelectedProfile(null);
    setDetailError(null);
  };

  const handleUpdateProfile = async (userId: string, request: UpdateProfileRequest) => {
    try {
      await updateProfileUseCase.execute(userId, request);
      alert("Profile updated successfully");
      // Refresh the profile data
      if (selectedProfile) {
        const updatedProfile = await getDetailUseCase.execute(userId);
        setSelectedProfile(updatedProfile);
      }
      // Refresh the list to show updated data
      loadProfiles();
    } catch (error: any) {
      throw new Error(error.message);
    }
  };

  const handleDeleteProfile = async (userId: string) => {
    try {
      await deleteProfileUseCase.execute(userId);
      alert("Profile deleted successfully");
      // Refresh the list to remove deleted profile
      loadProfiles();
    } catch (error: any) {
      throw new Error(error.message);
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
      <h1 style={{
        textAlign: 'center',
        fontSize: '2.5rem',
        fontWeight: '700',
        marginBottom: '2rem',
        background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)',
        WebkitBackgroundClip: 'text',
        WebkitTextFillColor: 'transparent',
        backgroundClip: 'text'
      }}>
        Profile Management
      </h1>

      <section style={{ marginBottom: '3rem' }}>
        <h2 style={{
          fontSize: '1.75rem',
          fontWeight: '700',
          marginBottom: '1.5rem',
          textAlign: 'center',
          background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          backgroundClip: 'text'
        }}>
          Create New Profile
        </h2>
        <ProfileForm
          values={values}
          loading={formLoading}
          error={formError}
          onChange={handleChange}
          onSubmit={handleSubmit}
        />
      </section>

      <section>
      <ProfileList
        profiles={profiles}
        loading={listLoading}
        error={listError}
        onRefresh={loadProfiles}
        onProfileClick={handleProfileClick}
        onDelete={handleDeleteProfile}
      />
      </section>

      <ProfileDetail
        profile={selectedProfile}
        loading={detailLoading}
        error={detailError}
        onClose={handleCloseDetail}
        onUpdate={handleUpdateProfile}
        onDelete={handleDeleteProfile}
      />
    </div>
  );
};
