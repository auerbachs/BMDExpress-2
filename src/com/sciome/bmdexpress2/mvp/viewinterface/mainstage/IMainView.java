package com.sciome.bmdexpress2.mvp.viewinterface.mainstage;

public interface IMainView
{

	public void updateProjectLabel(String label);

	public void updateSelectionLabel(String label);

	public void updateActionStatusLabel(String label);

	public void showErrorAlert(String getPayload);

	public void showMessageDialog(String getPayload);

	public void showWarningDialog(String getPayload);

}
