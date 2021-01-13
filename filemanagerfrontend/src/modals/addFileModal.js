import React from 'react';
import 'antd/dist/antd.css';
import { Input } from 'antd';
 
const AddFileModal = (props) => {
 return (
 <div>
 <input type="file" onChange={props.onFileChange}/>
 {props.newFile && props.newFile.name}
</div>
 );
};

export default AddFileModal;
 